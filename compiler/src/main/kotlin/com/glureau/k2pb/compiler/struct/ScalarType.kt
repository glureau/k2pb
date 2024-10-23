package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.ProtoIntegerType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asClassName

data class ScalarFieldType(
    val kotlinClass: ClassName,
    val protoType: ScalarType,
    val writeMethod: (fieldName: String, tag: Int) -> CodeBlock = { f, t -> CodeBlock.of("") },
    val writeMethodNoTag: (fieldName: String) -> CodeBlock = { f -> CodeBlock.of("") },
    val readMethod: () -> CodeBlock = { CodeBlock.of("") },
    val readMethodNoTag: () -> CodeBlock = { CodeBlock.of("") },
) : FieldType {
    companion object {
        val String = ScalarFieldType(
            kotlinClass = String::class.asClassName(),
            protoType = ScalarType.string,
            writeMethod = { f, t -> CodeBlock.of("writeString($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeString($f)") },
            readMethod = { CodeBlock.of("readString()") },
            readMethodNoTag = { CodeBlock.of("readStringNoTag()") },
        )
        val Int = ScalarFieldType(
            kotlinClass = Int::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f, $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f)") },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT)", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readInt32NoTag()") },
        )
        val Char = ScalarFieldType(
            kotlinClass = Char::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f.toInt())") },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT).toChar()", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag().toChar()") },
        )
        val Short = ScalarFieldType(
            kotlinClass = Short::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f.toInt())") },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT).toShort()", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag().toShort()") },
        )
        val Byte = ScalarFieldType(
            kotlinClass = Byte::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt($f.toInt())") },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT).toByte()", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag().toByte()") },
        )
        val Long = ScalarFieldType(
            kotlinClass = Long::class.asClassName(),
            protoType = ScalarType.int64,
            writeMethod = { f, t ->
                CodeBlock.of("writeLong($f, $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeLong($f)") },
            readMethod = { CodeBlock.of("readLong(%T.DEFAULT)", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readLongNoTag()") },
        )
        val Float = ScalarFieldType(
            kotlinClass = Float::class.asClassName(),
            protoType = ScalarType.float,
            writeMethod = { f, t -> CodeBlock.of("writeFloat($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeFloat($f)") },
            readMethod = { CodeBlock.of("readFloat()") },
            readMethodNoTag = { CodeBlock.of("readFloatNoTag()") },
        )
        val Double = ScalarFieldType(
            kotlinClass = Double::class.asClassName(),
            protoType = ScalarType.double,
            writeMethod = { f, t -> CodeBlock.of("writeDouble($f, $t)") },
            writeMethodNoTag = { f -> CodeBlock.of("writeDouble($f)") },
            readMethod = { CodeBlock.of("readDouble()") },
            readMethodNoTag = { CodeBlock.of("readDoubleNoTag()") },
        )
        val Boolean = ScalarFieldType(
            kotlinClass = Boolean::class.asClassName(),
            protoType = ScalarType.bool,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt(if ($f) 1 else 0, $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeInt(if ($f) 1 else 0)") },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT) == 1", ProtoIntegerType::class.asClassName()) },
            readMethodNoTag = { CodeBlock.of("readIntNoTag() == 1") },
        )
        val ByteArray = ScalarFieldType(
            kotlinClass = ByteArray::class.asClassName(),
            protoType = ScalarType.bytes,
            writeMethod = { f, t ->
                CodeBlock.of("writeBytes($f, $t)")
            },
            writeMethodNoTag = { f -> CodeBlock.of("writeBytes($f)") },
            readMethod = { CodeBlock.of("readByteArray()") },
            readMethodNoTag = { CodeBlock.of("readByteArrayNoTag()") },
        )

        // To avoid the dependency on kotlinx-datetime
        private val instantClassName = ClassName("kotlinx.datetime", "Instant")
        val Instant = ScalarFieldType(kotlinClass = instantClassName, protoType = ScalarType.string)
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