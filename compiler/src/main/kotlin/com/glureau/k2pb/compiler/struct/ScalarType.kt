package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.ProtoIntegerType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asClassName

data class ScalarFieldType(
    val kotlinClass: ClassName,
    val protoType: ScalarType,
    val writeMethod: (fieldName: String, tag: Int) -> CodeBlock = { f, t -> CodeBlock.of("") },
    val readMethod: () -> CodeBlock = { CodeBlock.of("") },
) : FieldType {
    companion object {
        val String = ScalarFieldType(
            kotlinClass = String::class.asClassName(),
            protoType = ScalarType.string,
            writeMethod = { f, t -> CodeBlock.of("writeString($f, $t)") },
            readMethod = { CodeBlock.of("readString()") }
        )
        val Int = ScalarFieldType(
            kotlinClass = Int::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f, $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT)", ProtoIntegerType::class.asClassName()) }
        )
        val Char = ScalarFieldType(
            kotlinClass = Char::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT).toChar()", ProtoIntegerType::class.asClassName()) }
        )
        val Short = ScalarFieldType(
            kotlinClass = Short::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT).toShort()", ProtoIntegerType::class.asClassName()) }
        )
        val Byte = ScalarFieldType(
            kotlinClass = Byte::class.asClassName(),
            protoType = ScalarType.int32,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt($f.toInt(), $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT).toByte()", ProtoIntegerType::class.asClassName()) }
        )
        val Long = ScalarFieldType(
            kotlinClass = Long::class.asClassName(),
            protoType = ScalarType.int64,
            writeMethod = { f, t ->
                CodeBlock.of("writeLong($f, $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            readMethod = { CodeBlock.of("readLong(%T.DEFAULT)", ProtoIntegerType::class.asClassName()) }
        )
        val Float = ScalarFieldType(
            kotlinClass = Float::class.asClassName(),
            protoType = ScalarType.float,
            writeMethod = { f, t -> CodeBlock.of("writeFloat($f, $t)") },
            readMethod = { CodeBlock.of("readFloat()") }
        )
        val Double = ScalarFieldType(
            kotlinClass = Double::class.asClassName(),
            protoType = ScalarType.double,
            writeMethod = { f, t -> CodeBlock.of("writeDouble($f, $t)") },
            readMethod = { CodeBlock.of("readDouble()") }
        )
        val Boolean = ScalarFieldType(
            kotlinClass = Boolean::class.asClassName(),
            protoType = ScalarType.bool,
            writeMethod = { f, t ->
                CodeBlock.of("writeInt(if ($f) 1 else 0, $t, %T.DEFAULT)", ProtoIntegerType::class.asClassName())
            },
            readMethod = { CodeBlock.of("readInt(%T.DEFAULT) == 1", ProtoIntegerType::class.asClassName()) }
        )
        val ByteArray = ScalarFieldType(
            kotlinClass = ByteArray::class.asClassName(),
            protoType = ScalarType.bytes,
            writeMethod = { f, t ->
                CodeBlock.of("writeBytes($f, $t)")
            },
            readMethod = { CodeBlock.of("readByteArray()") }
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