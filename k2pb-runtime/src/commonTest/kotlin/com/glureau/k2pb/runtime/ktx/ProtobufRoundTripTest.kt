package com.glureau.k2pb.runtime.ktx

import com.glureau.k2pb.ProtoIntegerType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * End-to-end round-trip tests: write with ProtobufWriterImpl, read with ProtobufReaderImpl.
 */
class ProtobufRoundTripTest {

    private fun encode(block: ProtobufWriterImpl.() -> Unit): ByteArray {
        val out = ByteArrayOutput()
        ProtobufWriterImpl(out).block()
        return out.toByteArray()
    }

    private fun decode(bytes: ByteArray): ProtobufReaderImpl {
        return ProtobufReaderImpl(ByteArrayInput(bytes))
    }

    // --- Int (varint) ---

    @Test
    fun int_zero() {
        val bytes = encode { writeInt(0, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        assertEquals(1, reader.readTag())
        assertEquals(0, reader.readInt(ProtoIntegerType.DEFAULT))
    }

    @Test
    fun int_positive() {
        val bytes = encode { writeInt(42, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(42, reader.readInt(ProtoIntegerType.DEFAULT))
    }

    @Test
    fun int_maxValue() {
        val bytes = encode { writeInt(Int.MAX_VALUE, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Int.MAX_VALUE, reader.readInt(ProtoIntegerType.DEFAULT))
    }

    @Test
    fun int_negative() {
        val bytes = encode { writeInt(-1, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(-1, reader.readInt(ProtoIntegerType.DEFAULT))
    }

    @Test
    fun int_minValue() {
        val bytes = encode { writeInt(Int.MIN_VALUE, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Int.MIN_VALUE, reader.readInt(ProtoIntegerType.DEFAULT))
    }

    // --- Int (signed / zigzag) ---

    @Test
    fun int_signed_positive() {
        val bytes = encode { writeInt(42, 1, ProtoIntegerType.SIGNED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(42, reader.readInt(ProtoIntegerType.SIGNED))
    }

    @Test
    fun int_signed_negative() {
        val bytes = encode { writeInt(-42, 1, ProtoIntegerType.SIGNED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(-42, reader.readInt(ProtoIntegerType.SIGNED))
    }

    // --- Int (fixed) ---

    @Test
    fun int_fixed() {
        val bytes = encode { writeInt(0x12345678, 1, ProtoIntegerType.FIXED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(0x12345678, reader.readInt(ProtoIntegerType.FIXED))
    }

    // --- Long ---

    @Test
    fun long_zero() {
        val bytes = encode { writeLong(0L, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(0L, reader.readLong(ProtoIntegerType.DEFAULT))
    }

    @Test
    fun long_maxValue() {
        val bytes = encode { writeLong(Long.MAX_VALUE, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Long.MAX_VALUE, reader.readLong(ProtoIntegerType.DEFAULT))
    }

    @Test
    fun long_negative() {
        val bytes = encode { writeLong(-1L, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(-1L, reader.readLong(ProtoIntegerType.DEFAULT))
    }

    @Test
    fun long_minValue() {
        val bytes = encode { writeLong(Long.MIN_VALUE, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Long.MIN_VALUE, reader.readLong(ProtoIntegerType.DEFAULT))
    }

    @Test
    fun long_signed_negative() {
        val bytes = encode { writeLong(-123456789L, 1, ProtoIntegerType.SIGNED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(-123456789L, reader.readLong(ProtoIntegerType.SIGNED))
    }

    @Test
    fun long_fixed() {
        val bytes = encode { writeLong(0x123456789ABCDEF0L, 1, ProtoIntegerType.FIXED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(0x123456789ABCDEF0L, reader.readLong(ProtoIntegerType.FIXED))
    }

    // --- Float ---

    @Test
    fun float_zero() {
        val bytes = encode { writeFloat(0.0f, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(0.0f, reader.readFloat())
    }

    @Test
    fun float_positive() {
        val bytes = encode { writeFloat(3.14f, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(3.14f, reader.readFloat())
    }

    @Test
    fun float_negative() {
        val bytes = encode { writeFloat(-1.5f, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(-1.5f, reader.readFloat())
    }

    @Test
    fun float_nan() {
        val bytes = encode { writeFloat(Float.NaN, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertTrue(reader.readFloat().isNaN())
    }

    @Test
    fun float_infinity() {
        val bytes = encode { writeFloat(Float.POSITIVE_INFINITY, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Float.POSITIVE_INFINITY, reader.readFloat())
    }

    // --- Double ---

    @Test
    fun double_zero() {
        val bytes = encode { writeDouble(0.0, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(0.0, reader.readDouble())
    }

    @Test
    fun double_positive() {
        val bytes = encode { writeDouble(3.141592653589793, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(3.141592653589793, reader.readDouble())
    }

    @Test
    fun double_negative() {
        val bytes = encode { writeDouble(-1.5, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(-1.5, reader.readDouble())
    }

    @Test
    fun double_nan() {
        val bytes = encode { writeDouble(Double.NaN, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertTrue(reader.readDouble().isNaN())
    }

    // --- String ---

    @Test
    fun string_empty() {
        val bytes = encode { writeString("", 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals("", reader.readString())
    }

    @Test
    fun string_ascii() {
        val bytes = encode { writeString("hello", 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals("hello", reader.readString())
    }

    @Test
    fun string_unicode() {
        val bytes = encode { writeString("hello \uD83D\uDE00", 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals("hello \uD83D\uDE00", reader.readString())
    }

    // --- Bytes ---

    @Test
    fun bytes_empty() {
        val bytes = encode { writeBytes(byteArrayOf(), 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertTrue(reader.readByteArray().isEmpty())
    }

    @Test
    fun bytes_data() {
        val data = byteArrayOf(0, 1, 127, -128, -1)
        val bytes = encode { writeBytes(data, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertTrue(reader.readByteArray().contentEquals(data))
    }

    // --- Multiple fields ---

    @Test
    fun multipleFields() {
        val bytes = encode {
            writeInt(42, 1, ProtoIntegerType.DEFAULT)
            writeString("test", 2)
            writeDouble(1.5, 3)
        }
        val reader = decode(bytes)

        assertEquals(1, reader.readTag())
        assertEquals(42, reader.readInt(ProtoIntegerType.DEFAULT))

        assertEquals(2, reader.readTag())
        assertEquals("test", reader.readString())

        assertEquals(3, reader.readTag())
        assertEquals(1.5, reader.readDouble())

        assertTrue(reader.eof)
    }

    // --- Skip ---

    @Test
    fun skipElement_varint() {
        val bytes = encode {
            writeInt(42, 1, ProtoIntegerType.DEFAULT)
            writeInt(99, 2, ProtoIntegerType.DEFAULT)
        }
        val reader = decode(bytes)
        reader.readTag()
        reader.skipElement() // skip field 1
        assertEquals(2, reader.readTag())
        assertEquals(99, reader.readInt(ProtoIntegerType.DEFAULT))
    }

    @Test
    fun skipElement_sizeDelimited() {
        val bytes = encode {
            writeString("skip me", 1)
            writeInt(99, 2, ProtoIntegerType.DEFAULT)
        }
        val reader = decode(bytes)
        reader.readTag()
        reader.skipElement()
        assertEquals(2, reader.readTag())
        assertEquals(99, reader.readInt(ProtoIntegerType.DEFAULT))
    }

    // --- Tag / pushback ---

    @Test
    fun pushBackTag() {
        val bytes = encode {
            writeInt(42, 1, ProtoIntegerType.DEFAULT)
            writeInt(99, 2, ProtoIntegerType.DEFAULT)
        }
        val reader = decode(bytes)
        assertEquals(1, reader.readTag())
        reader.pushBackTag()
        // Should re-read tag 1
        assertEquals(1, reader.readTag())
        assertEquals(42, reader.readInt(ProtoIntegerType.DEFAULT))
    }

    // --- EOF ---

    @Test
    fun eof_emptyInput() {
        val reader = decode(byteArrayOf())
        assertTrue(reader.eof)
    }

    @Test
    fun readTag_eof_returnsMinusOne() {
        val reader = decode(byteArrayOf())
        assertEquals(-1, reader.readTag())
    }
}
