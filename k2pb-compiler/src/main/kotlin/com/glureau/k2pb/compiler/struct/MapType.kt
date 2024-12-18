package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.poet.ProtoIntegerTypeDefault
import com.glureau.k2pb.compiler.poet.readMessageExt
import com.glureau.k2pb.compiler.poet.writeMessageExt
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec

data class MapType(
    val keyType: FieldType,
    val valueType: FieldType,
    override val isNullable: Boolean = false
) : FieldType

fun StringBuilder.appendKotlinMapDefinition(type: MapType) = apply {
    append(
        "Map<${appendKotlinDefinition(type.keyType)}, ${appendKotlinDefinition(type.valueType)}>"
                + if (type.isNullable) "?" else ""
    )
}

fun FunSpec.Builder.encodeMapType(instanceName: String, fieldName: String, type: MapType, tag: Int) {
    beginControlFlow("$instanceName.${fieldName}.forEach")
    beginControlFlow("%M($tag)", writeMessageExt)
    if (type.keyType.useEnumAsKey()) {
        addCode(
            CodeBlock.of(
                "writeInt(it.key.ordinal, 1, %T)\n",
                ProtoIntegerTypeDefault
            )
        )
    } else {
        addCode(type.keyType.write("it.key", 1))
    }
    addCode(type.valueType.write("it.value", 2))
    endControlFlow() // writeMessage of the item
    endControlFlow() // forEach
}

// Enum not supported for keys, what we can do is use the enum ordinal in a best-effort approach,
// also it doesn't offer the same guarantees...
internal fun FieldType.useEnumAsKey(): Boolean = this is ReferenceType && this.isEnum

private fun FieldType.write(name: String, tag: Int): CodeBlock =
    when (this) {
        is ScalarFieldType -> safeWriteMethod(name, tag, null, true)
        is ReferenceType -> CodeBlock.of(
            "writeMessage(%L) { with(protoSerializer) { encode(%L, %T::class) } }\n",
            tag,
            name,
            className
        )

        else -> CodeBlock.of("Map key or value cannot be a reference type name=$name, tag=$tag")
    }

fun FunSpec.Builder.decodeMapTypeVariableDefinition(fieldName: String, type: MapType) {
    val typeName = StringBuilder().appendKotlinDefinition(type)
    addStatement("val ${fieldName}: Mutable${typeName} = mutableMapOf()")
}

fun FunSpec.Builder.decodeMapType(fieldName: String, type: MapType) {
    beginControlFlow("%M()", readMessageExt)
    /// TODO: We may write it differently so that we can swap the key and value... (warning on perf tho)
    addStatement("readTag()") // Should be 1
    addCode("val key = ")
    if (type.keyType.useEnumAsKey()) {
        addCode(
            CodeBlock.of(
                "%T.entries[readInt(%T)]",
                (type.keyType as ReferenceType).className,
                ProtoIntegerTypeDefault
            )
        )
    } else {
        addCode(type.keyType.readNoTag())
    }
    addStatement("")

    addStatement("readTag()") // Should be 2
    addCode("val value = ")
    addCode(type.valueType.readNoTag())
    addStatement("")
    addStatement("${fieldName}[key] = value")
    endControlFlow()
}