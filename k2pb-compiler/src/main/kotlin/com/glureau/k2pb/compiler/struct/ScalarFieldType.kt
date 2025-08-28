package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.customConverterType
import com.glureau.k2pb.compiler.poet.ProtoIntegerTypeDefault
import com.glureau.k2pb.runtime.DefaultCodecImpl
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

data class ScalarFieldType(
    val kotlinClass: ClassName,
    val protoType: ScalarType,
    val shouldEncodeDefault: (fieldName: String) -> String,
    val defaultValue: String,
    private val writeMethod: (fieldName: String, tag: Int) -> CodeBlock,
    private val writeMethodNoTag: (fieldName: String) -> CodeBlock,
    val readMethod: () -> CodeBlock,
    val readMethodNoTag: () -> CodeBlock,
    override val isNullable: Boolean = false,
) : FieldType {
    val safeWriteMethod: (fieldName: String, tag: Int, forceEncodeDefault: Boolean) -> CodeBlock =
        { f, t, fed -> safeWrite(f, fed) { writeMethod(f, t) } }
    val safeWriteMethodNoTag: (fieldName: String, forceEncodeDefault: Boolean) -> CodeBlock =
        { f, fed -> safeWrite(f, fed) { writeMethodNoTag(f) } }

    private fun safeWrite(fieldName: String, forceEncodeDefault: Boolean, method: () -> CodeBlock) =
        CodeBlock.builder()
            .also { builder -> if (isNullable) builder.beginControlFlow("if ($fieldName != null)") }
            .also { if (!forceEncodeDefault) it.beginControlFlow("if (${shouldEncodeDefault(fieldName)})") }
            .add(method())
            .addStatement("")
            .also {
                if (!forceEncodeDefault)
                    it.endControlFlow()
                        .addStatement("// else: default value should not be encoded")
            }
            .apply {
                if (isNullable) {
                    endControlFlow()
                }
            }
            .build()

    companion object {
        val String = ScalarFieldType(
            kotlinClass = kotlin.String::class.asClassName(),
            shouldEncodeDefault = { "$it != \"\"" },
            defaultValue = "\"\"",
            protoType = ScalarType.string,
            writeMethod = { f, t -> CodeBlock.of("writeString($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeString($f)") },
            readMethod = { CodeBlock.of("readString()") },
            readMethodNoTag = { CodeBlock.of("readStringNoTag()") },
        )
        val StringNullable = String.copy(isNullable = true)
        val Int = ScalarFieldType(
            kotlinClass = kotlin.Int::class.asClassName(),
            shouldEncodeDefault = { "$it != 0" },
            defaultValue = "0",
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
            shouldEncodeDefault = { "$it != 0.toChar()" },
            defaultValue = "0.toChar()",
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
            shouldEncodeDefault = { "$it != 0.toShort()" },
            defaultValue = "0.toShort()",
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
            shouldEncodeDefault = { "$it != 0.toByte()" },
            defaultValue = "0.toByte()",
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
            shouldEncodeDefault = { "$it != 0L" },
            defaultValue = "0L",
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
            shouldEncodeDefault = { "$it != 0.0f" },
            defaultValue = "0.0f",
            protoType = ScalarType.float,
            writeMethod = { f, t -> CodeBlock.of("writeFloat($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeFloat($f)") },
            readMethod = { CodeBlock.of("readFloat()") },
            readMethodNoTag = { CodeBlock.of("readFloatNoTag()") },
        )
        val FloatNullable = Float.copy(isNullable = true)
        val Double = ScalarFieldType(
            kotlinClass = kotlin.Double::class.asClassName(),
            shouldEncodeDefault = { "$it != 0.0" },
            defaultValue = "0.0",
            protoType = ScalarType.double,
            writeMethod = { f, t -> CodeBlock.of("writeDouble($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeDouble($f)") },
            readMethod = { CodeBlock.of("readDouble()") },
            readMethodNoTag = { CodeBlock.of("readDoubleNoTag()") },
        )
        val DoubleNullable = Double.copy(isNullable = true)
        val Boolean = ScalarFieldType(
            kotlinClass = kotlin.Boolean::class.asClassName(),
            shouldEncodeDefault = { "$it != false" },
            defaultValue = "false",
            protoType = ScalarType.bool,
            writeMethod = { f, t ->
                // As 'false' should not be encoded (default value), we will always call this method for "true"
                //CodeBlock.of("writeInt(if ($f) 1 else 0, $t, %T)", ProtoIntegerTypeDefault)
                CodeBlock.of("writeInt(1, $t, %T)", ProtoIntegerTypeDefault)
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt(if ($f) 1 else 0)") },
            // '\n' are used because '·' is still wrapped even if it shouldn't...
            readMethod = { CodeBlock.of("\nreadInt(%T)·==·1", ProtoIntegerTypeDefault) },
            readMethodNoTag = { CodeBlock.of("\nreadIntNoTag()·==·1") },
        )
        val BooleanNullable = Boolean.copy(isNullable = true)
        val ByteArray = ScalarFieldType(
            kotlinClass = kotlin.ByteArray::class.asClassName(),
            shouldEncodeDefault = { "$it.isNotEmpty()" },
            defaultValue = "byteArrayOf()",
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


fun FunSpec.Builder.encodeScalarFieldType(
    instanceName: String,
    fieldName: String,
    fieldType: ScalarFieldType,
    tag: Int,
    annotatedCodec: KSType?,
    nullabilitySubField: NullabilitySubField?,
) {
    (annotatedCodec?.let { s ->
        val encodedTmpName = "${fieldName.replace(".", "_")}Encoded"
        addStatement(
            "val $encodedTmpName = %T().encode($instanceName.${fieldName}, %T(protoCodec))",
            s.toClassName(),
            DefaultCodecImpl::class.asClassName()
        )
        addCode(fieldType.safeWriteMethod(encodedTmpName, tag, false))
    } ?: addCode(fieldType.safeWriteMethod("$instanceName.${fieldName}", tag, false)))
        .also { addStatement("") }
}

fun FunSpec.Builder.decodeScalarTypeVariableDefinition(
    fieldName: String,
    type: ScalarFieldType,
    annotatedCodec: KSType?,
    nullabilitySubField: NullabilitySubField?,
) {
    annotatedCodec.customConverterType()?.let { customCodecType ->
        // TODO: String should be derived from customCodecType
        addStatement("var $fieldName: String? = null")
    } ?: run {
        addStatement("var $fieldName: %T? = null", type.kotlinClass)
        if (nullabilitySubField != null) {
            addNullabilityStatement(nullabilitySubField)
        }
    }
}

fun FunSpec.Builder.decodeScalarType(fieldName: String, type: ScalarFieldType, annotatedCodec: KSType?) {
    if (annotatedCodec != null) TODO("Not supported yet")
    addStatement("$fieldName = ${type.readMethod()}")
}