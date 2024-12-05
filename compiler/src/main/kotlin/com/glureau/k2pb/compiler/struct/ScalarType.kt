package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.CustomStringConverter
import com.glureau.k2pb.compiler.poet.ProtoIntegerTypeDefault
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
    private val writeMethod: (fieldName: String, tag: Int) -> CodeBlock,
    private val writeMethodNoTag: (fieldName: String) -> CodeBlock,
    val readMethod: () -> CodeBlock,
    val readMethodNoTag: () -> CodeBlock,
    override val isNullable: Boolean = false,
) : FieldType {
    val safeWriteMethod: (fieldName: String, tag: Int, nullableTag: Int?) -> CodeBlock =
        { f, t, nt -> safeWrite(f, nt) { writeMethod(f, t) } }
    val safeWriteMethodNoTag: (fieldName: String, nullableTag: Int?) -> CodeBlock =
        { f, nt -> safeWrite(f, nt) { writeMethodNoTag(f) } }

    private fun safeWrite(fieldName: String, nullableTag: Int?, method: () -> CodeBlock) =
        if (isNullable) {
            CodeBlock.builder()
                .beginControlFlow("if ($fieldName != null)")
                .add(method())
                .endControlFlow()
                .also {
                    if (nullableTag != null) {
                        it.beginControlFlow("else")
                            .addStatement(
                                "writeInt(value = 1, tag = $nullableTag, format = %T)",
                                ProtoIntegerTypeDefault
                            )
                            .endControlFlow()
                    }
                }
                .build()
        } else {
            method()
        }

    companion object {
        val String = ScalarFieldType(
            kotlinClass = kotlin.String::class.asClassName(),
            protoType = ScalarType.string,
            writeMethod = { f, t -> CodeBlock.of("writeString($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeString($f)") },
            readMethod = { CodeBlock.of("readString()") },
            readMethodNoTag = { CodeBlock.of("readStringNoTag()") },
        )
        val StringNullable = String.copy(isNullable = true)
        val Int = ScalarFieldType(
            kotlinClass = kotlin.Int::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f, $t, %T)", ProtoIntegerTypeDefault)
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f)") },
            readMethod = { CodeBlock.of("readInt(%T)", ProtoIntegerTypeDefault) },
            readMethodNoTag = { CodeBlock.of("readInt32NoTag()") },
        )
        val IntNullable = Int.copy(isNullable = true)
        val Char = ScalarFieldType(
            kotlinClass = kotlin.Char::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.code, $t, %T)", ProtoIntegerTypeDefault)
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f.code)") },
            readMethod = { CodeBlock.of("readInt(%T).toChar()", ProtoIntegerTypeDefault) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag().toChar()") },
        )
        val CharNullable = Char.copy(isNullable = true)
        val Short = ScalarFieldType(
            kotlinClass = kotlin.Short::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T)", ProtoIntegerTypeDefault)
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f.toInt())") },
            readMethod = { CodeBlock.of("readInt(%T).toShort()", ProtoIntegerTypeDefault) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag().toShort()") },
        )
        val ShortNullable = Short.copy(isNullable = true)
        val Byte = ScalarFieldType(
            kotlinClass = kotlin.Byte::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T)", ProtoIntegerTypeDefault)
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f.toInt())") },
            readMethod = { CodeBlock.of("readInt(%T).toByte()", ProtoIntegerTypeDefault) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag().toByte()") },
        )
        val ByteNullable = Byte.copy(isNullable = true)
        val Long = ScalarFieldType(
            kotlinClass = kotlin.Long::class.asClassName(),
            protoType = ScalarType.int64,
            writeMethod = { f, t ->
                CodeBlock.of("writeLong($f, $t, %T)", ProtoIntegerTypeDefault)
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeLong($f)") },
            readMethod = { CodeBlock.of("readLong(%T)", ProtoIntegerTypeDefault) },
            readMethodNoTag = { CodeBlock.of("readLongNoTag()") },
        )
        val LongNullable = Long.copy(isNullable = true)
        val Float = ScalarFieldType(
            kotlinClass = kotlin.Float::class.asClassName(),
            protoType = ScalarType.float,
            writeMethod = { f, t -> CodeBlock.of("writeFloat($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeFloat($f)") },
            readMethod = { CodeBlock.of("readFloat()") },
            readMethodNoTag = { CodeBlock.of("readFloatNoTag()") },
        )
        val FloatNullable = Float.copy(isNullable = true)
        val Double = ScalarFieldType(
            kotlinClass = kotlin.Double::class.asClassName(),
            protoType = ScalarType.double,
            writeMethod = { f, t -> CodeBlock.of("writeDouble($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeDouble($f)") },
            readMethod = { CodeBlock.of("readDouble()") },
            readMethodNoTag = { CodeBlock.of("readDoubleNoTag()") },
        )
        val DoubleNullable = Double.copy(isNullable = true)
        val Boolean = ScalarFieldType(
            kotlinClass = kotlin.Boolean::class.asClassName(),
            protoType = ScalarType.bool,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt(if ($f) 1 else 0, $t, %T)", ProtoIntegerTypeDefault)
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt(if ($f) 1 else 0)") },
            // '\n' are used because '·' is still wrapped even if it shouldn't...
            readMethod = { CodeBlock.of("\nreadInt(%T)·==·1 /* ooo */", ProtoIntegerTypeDefault) },
            readMethodNoTag = { CodeBlock.of("\nreadIntNoTag()·==·1") },
        )
        val BooleanNullable = Boolean.copy(isNullable = true)
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
        val ByteArrayNullable = ByteArray.copy(isNullable = true)
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
    annotatedSerializer: KSType?,
    nullabilitySubField: NullabilitySubField?
) {
    (annotatedSerializer?.let { s ->
        val encodedTmpName = "${fieldName.replace(".", "_")}Encoded"
        addStatement(
            "val $encodedTmpName = %T().encode(instance.${fieldName})",
            s.toClassName()
        )
        addCode(fieldType.safeWriteMethod(encodedTmpName, tag, nullabilitySubField?.protoNumber))
    } ?: addCode(fieldType.safeWriteMethod("instance.${fieldName}", tag, nullabilitySubField?.protoNumber)))
        .also { addStatement("") }
}

fun FunSpec.Builder.decodeScalarTypeVariableDefinition(
    fieldName: String,
    type: ScalarFieldType,
    annotatedSerializer: KSType?,
    nullabilitySubField: NullabilitySubField?
) {
    annotatedSerializer?.let { annSerializer ->
        val parents = (annSerializer.declaration as KSClassDeclaration)
            .superTypes
            .map { it.resolve().toClassName() }
        if (parents.contains(CustomStringConverter::class.asClassName())) {
            addStatement("var $fieldName: String? = null")
        } else {
            TODO("Not supported yet")
        }
        // TODO: Support nullability subfield with annotated serializer

    } ?: run {
        addStatement("var $fieldName: %T? = null", type.kotlinClass)
        if (nullabilitySubField != null) {
            addStatement("var ${nullabilitySubField.fieldName}: Boolean = false")
        }
    }
}

fun FunSpec.Builder.decodeScalarType(fieldName: String, type: ScalarFieldType, annotatedSerializer: KSType?) {
    if (annotatedSerializer != null) TODO("Not supported yet")
    addStatement("${fieldName} = ${type.readMethod()}")
}