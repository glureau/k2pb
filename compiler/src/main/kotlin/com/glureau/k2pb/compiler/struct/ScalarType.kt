package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.CustomStringConverter
import com.glureau.k2pb.ProtoIntegerType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

data class ScalarFieldType(
    val kotlinClass: ClassName,
    val protoType: ScalarType,
    val writeMethod: (fieldName: String, tag: Int) -> CodeBlock = { f, t -> CodeBlock.of("") },
    val writeMethodNoTag: (fieldName: String) -> CodeBlock = { f -> CodeBlock.of("") },
    val readMethod: () -> CodeBlock = { CodeBlock.of("") },
    val readMethodNoTag: () -> CodeBlock = { CodeBlock.of("") },
    override val isNullable: Boolean = false,
) : FieldType {
    companion object {
        val String = ScalarFieldType(
            kotlinClass = kotlin.String::class.asClassName(),
            protoType = ScalarType.string,
            writeMethod = { f, t -> CodeBlock.of("writeString($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeString($f)") },
            readMethod = { CodeBlock.of("readString()") },
            readMethodNoTag = { CodeBlock.of("readStringNoTag()") },
        )
        val Int = ScalarFieldType(
            kotlinClass = kotlin.Int::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f, $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f)") },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT)", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readInt32NoTag()") },
        )
        val Char = ScalarFieldType(
            kotlinClass = kotlin.Char::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f.toInt())") },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT).toChar()", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag().toChar()") },
        )
        val Short = ScalarFieldType(
            kotlinClass = kotlin.Short::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f.toInt())") },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT).toShort()", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag().toShort()") },
        )
        val Byte = ScalarFieldType(
            kotlinClass = kotlin.Byte::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f.toInt())") },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT).toByte()", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag().toByte()") },
        )
        val Long = ScalarFieldType(
            kotlinClass = kotlin.Long::class.asClassName(),
            protoType = ScalarType.int64,
            writeMethod = { f, t ->
                CodeBlock.of("writeLong($f, $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeLong($f)") },
            readMethod = { CodeBlock.of("readLong(%T.DEFAULT)", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readLongNoTag()") },
        )
        val Float = ScalarFieldType(
            kotlinClass = kotlin.Float::class.asClassName(),
            protoType = ScalarType.float,
            writeMethod = { f, t -> CodeBlock.of("writeFloat($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeFloat($f)") },
            readMethod = { CodeBlock.of("readFloat()") },
            readMethodNoTag = { CodeBlock.of("readFloatNoTag()") },
        )
        val Double = ScalarFieldType(
            kotlinClass = kotlin.Double::class.asClassName(),
            protoType = ScalarType.double,
            writeMethod = { f, t -> CodeBlock.of("writeDouble($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeDouble($f)") },
            readMethod = { CodeBlock.of("readDouble()") },
            readMethodNoTag = { CodeBlock.of("readDoubleNoTag()") },
        )
        val Boolean = ScalarFieldType(
            kotlinClass = kotlin.Boolean::class.asClassName(),
            protoType = ScalarType.bool,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt(if ($f) 1 else 0, $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt(if ($f) 1 else 0)") },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT) == 1", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag() == 1") },
        )
        val ByteArray = ScalarFieldType(
            kotlinClass = kotlin.ByteArray::class.asClassName(),
            protoType = ScalarType.bytes,
            writeMethod = { f, t ->
                CodeBlock.of("writeBytes($f, $t)")
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeBytes($f)") },
            readMethod = { CodeBlock.of("readByteArray()") },
            readMethodNoTag = { CodeBlock.of("readByteArrayNoTag()") },
        )
    }
}

enum class ScalarType { // https://protobuf.dev/programming-guides/proto3/#scalar
    double,
    float,
    int32,
    int64,
    uint32,
    uint64,
    sint32,
    sint64,
    fixed32,
    fixed64,
    sfixed32,
    sfixed64,
    bool,
    string,
    bytes,
}

fun StringBuilder.appendScalarType(type: ScalarFieldType) {
    append(type.protoType.name)
}


fun FunSpec.Builder.encodeScalarFieldType(
    fieldName: String,
    fieldType: ScalarFieldType,
    tag: Int,
    annotatedSerializer: KSType?
) {
    (annotatedSerializer?.let { s ->
        val encodedTmpName = "${fieldName.replace(".", "_")}Encoded"
        addStatement(
            "val $encodedTmpName = %T().encode(instance.${fieldName})",
            s.toClassName()
        )
        addCode(fieldType.writeMethod(encodedTmpName, tag))
    } ?: addCode(fieldType.writeMethod("instance.${fieldName}", tag)))
        .also { addStatement("") }
}

fun FunSpec.Builder.decodeScalarTypeVariableDefinition(
    fieldName: String,
    type: ScalarFieldType,
    annotatedSerializer: KSType?
) {
    val typeName = type.kotlinClass.canonicalName
    annotatedSerializer?.let { annSerializer ->
        val parents = (annSerializer.declaration as KSClassDeclaration)
            .superTypes
            .map { it.resolve().toClassName() }
        if (parents.contains(CustomStringConverter::class.asClassName())) {
            addStatement("var $fieldName: String? = null")
        } else {
            TODO("Not supported yet")
        }
    } ?: run {
        addStatement("var $fieldName: $typeName? = null")
    }
}

fun FunSpec.Builder.decodeScalarType(fieldName: String, type: ScalarFieldType, annotatedSerializer: KSType?) {
    if (annotatedSerializer != null) TODO("Not supported yet")
    addStatement("${fieldName} = ${type.readMethod()}")
}