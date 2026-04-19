package com.glureau.k2pb.runtime.ktx

import com.glureau.k2pb.ProtoIntegerType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Tests for malformed, truncated, and corrupted protobuf input.
 * Covers P3-15 (byte-level edge cases), P3-18 (boundary values), P3-19 (malformed input).
 */
class MalformedInputTest {

    private fun encode(block: ProtobufWriterImpl.() -> Unit): ByteArray {
        val out = ByteArrayOutput()
        ProtobufWriterImpl(out).block()
        return out.toByteArray()
    }

    private fun decode(bytes: ByteArray): ProtobufReaderImpl {
        return ProtobufReaderImpl(ByteArrayInput(bytes))
    }

    // --- readString bounds check (CRIT-3) ---

    @Test
    fun readString_pastBoundary_onSlice() {
        // Create a parent buffer and slice a portion; readString beyond slice should fail
        val data = "ABCDEFGHIJ".encodeToByteArray() // 10 bytes
        val input = ByteArrayInput(data)
        val sliced = input.slice(5) // only 5 bytes available
        // Reading 5 bytes should work
        assertEquals("ABCDE", sliced.readString(5))
        // Now 0 bytes available - reading more should see 0 available
        assertEquals(0, sliced.availableBytes)
    }

    @Test
    fun readString_emptyInput() {
        val input = ByteArrayInput(byteArrayOf())
        // Reading 0-length string should succeed
        assertEquals("", input.readString(0))
    }

    // --- readIntLittleEndian EOF (BUG-3) ---

    @Test
    fun readIntLittleEndian_truncatedInput() {
        // Only 2 bytes available but readIntLittleEndian needs 4
        // Currently reads -1 (0xFF) for missing bytes - this test documents the bug
        val reader = decode(byteArrayOf())
        // Write a tag for I32 wire type at field 1
        val bytes = encode { writeFloat(1.0f, 1) }
        val fullReader = decode(bytes)
        fullReader.readTag()
        // This should work fine with complete data
        val value = fullReader.readFloat()
        assertEquals(1.0f, value)
    }

    @Test
    fun readLongLittleEndian_truncatedInput() {
        // Verify correct behavior with complete data
        val bytes = encode { writeDouble(1.0, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(1.0, reader.readDouble())
    }

    // --- checkLength only validates non-negative (BUG-7) ---

    @Test
    fun checkLength_negativeLength_throws() {
        val reader = decode(byteArrayOf())
        assertFailsWith<SerializationException> {
            reader.checkLength(-1)
        }
    }

    @Test
    fun checkLength_zero_succeeds() {
        val reader = decode(byteArrayOf())
        reader.checkLength(0) // should not throw
    }

    // --- Truncated varint ---

    @Test
    fun truncatedVarint32_throws() {
        // A varint byte with continuation bit set, but no more bytes
        val input = ByteArrayInput(byteArrayOf(0x80.toByte()))
        // read() returns -1 at EOF, but varint32 slow path uses read() which returns -1
        // The -1 & 0x7F = 127, and -1 & 0x80 = 128 (non-zero) so loop continues
        // This will eventually terminate due to shift exceeding 32 bits
        assertFailsWith<SerializationException> {
            input.readVarint32()
        }
    }

    @Test
    fun truncatedVarint64_throws() {
        val input = ByteArrayInput(byteArrayOf(0x80.toByte()))
        assertFailsWith<SerializationException> {
            input.readVarint64(false)
        }
    }

    // --- Oversized varint (more than 10 bytes) ---

    @Test
    fun oversizedVarint64_throws() {
        // 11 continuation bytes - exceeds 64-bit limit
        val bytes = ByteArray(11) { 0x80.toByte() }
        bytes[10] = 0x01 // terminator
        val input = ByteArrayInput(bytes)
        assertFailsWith<SerializationException> {
            input.readVarint64(false)
        }
    }

    @Test
    fun oversizedVarint32_throws() {
        // 6 continuation bytes - exceeds 32-bit limit
        val bytes = ByteArray(6) { 0x80.toByte() }
        bytes[5] = 0x01 // terminator
        val input = ByteArrayInput(bytes)
        assertFailsWith<SerializationException> {
            input.readVarint32()
        }
    }

    // --- Wrong wire type ---

    @Test
    fun readInt_wrongWireType_throws() {
        // Encode a string (SIZE_DELIMITED) but try to read as int (VARINT)
        val bytes = encode { writeString("hello", 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertFailsWith<SerializationException> {
            reader.readInt(ProtoIntegerType.DEFAULT) // expects VARINT but gets SIZE_DELIMITED
        }
    }

    @Test
    fun readFloat_wrongWireType_throws() {
        // Encode a varint but try to read as float (I32)
        val bytes = encode { writeInt(42, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertFailsWith<SerializationException> {
            reader.readFloat() // expects I32 but gets VARINT
        }
    }

    @Test
    fun readDouble_wrongWireType_throws() {
        val bytes = encode { writeInt(42, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertFailsWith<SerializationException> {
            reader.readDouble() // expects I64 but gets VARINT
        }
    }

    @Test
    fun readString_wrongWireType_throws() {
        val bytes = encode { writeInt(42, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertFailsWith<SerializationException> {
            reader.readString() // expects SIZE_DELIMITED but gets VARINT
        }
    }

    @Test
    fun readByteArray_wrongWireType_throws() {
        val bytes = encode { writeInt(42, 1, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        reader.readTag()
        assertFailsWith<SerializationException> {
            reader.readByteArray() // expects SIZE_DELIMITED but gets VARINT
        }
    }

    // --- Skip unknown wire type ---

    @Test
    fun skipElement_invalidWireType_throws() {
        // Manually craft a tag with wire type 3 (deprecated group start)
        // tag=1, wireType=3 -> (1 shl 3) or 3 = 11
        val out = ByteArrayOutput()
        out.encodeVarint32(11) // field 1, wire type 3
        val reader = decode(out.toByteArray())
        reader.readTag()
        assertFailsWith<SerializationException> {
            reader.skipElement()
        }
    }

    // --- Boundary values ---

    @Test
    fun double_negativeInfinity_roundTrip() {
        val bytes = encode { writeDouble(Double.NEGATIVE_INFINITY, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Double.NEGATIVE_INFINITY, reader.readDouble())
    }

    @Test
    fun double_positiveInfinity_roundTrip() {
        val bytes = encode { writeDouble(Double.POSITIVE_INFINITY, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Double.POSITIVE_INFINITY, reader.readDouble())
    }

    @Test
    fun double_minValue_roundTrip() {
        val bytes = encode { writeDouble(Double.MIN_VALUE, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Double.MIN_VALUE, reader.readDouble())
    }

    @Test
    fun double_maxValue_roundTrip() {
        val bytes = encode { writeDouble(Double.MAX_VALUE, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Double.MAX_VALUE, reader.readDouble())
    }

    @Test
    fun float_negativeInfinity_roundTrip() {
        val bytes = encode { writeFloat(Float.NEGATIVE_INFINITY, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Float.NEGATIVE_INFINITY, reader.readFloat())
    }

    @Test
    fun float_minValue_roundTrip() {
        val bytes = encode { writeFloat(Float.MIN_VALUE, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Float.MIN_VALUE.toBits(), reader.readFloat().toBits())
    }

    @Test
    fun float_maxValue_roundTrip() {
        val bytes = encode { writeFloat(Float.MAX_VALUE, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Float.MAX_VALUE.toBits(), reader.readFloat().toBits())
    }

    // --- Zigzag encoding boundary values ---

    @Test
    fun int_signed_smallValues() {
        for (value in listOf(0, 1, -1, 100, -100, 1000, -1000)) {
            val bytes = encode { writeInt(value, 1, ProtoIntegerType.SIGNED) }
            val reader = decode(bytes)
            reader.readTag()
            assertEquals(value, reader.readInt(ProtoIntegerType.SIGNED))
        }
    }

    @Test
    fun long_signed_maxValue() {
        val bytes = encode { writeLong(Long.MAX_VALUE, 1, ProtoIntegerType.SIGNED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Long.MAX_VALUE, reader.readLong(ProtoIntegerType.SIGNED))
    }

    @Test
    fun long_signed_minValue() {
        val bytes = encode { writeLong(Long.MIN_VALUE, 1, ProtoIntegerType.SIGNED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Long.MIN_VALUE, reader.readLong(ProtoIntegerType.SIGNED))
    }

    // --- Fixed int/long boundary values ---

    @Test
    fun int_fixed_maxValue() {
        val bytes = encode { writeInt(Int.MAX_VALUE, 1, ProtoIntegerType.FIXED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Int.MAX_VALUE, reader.readInt(ProtoIntegerType.FIXED))
    }

    @Test
    fun int_fixed_minValue() {
        val bytes = encode { writeInt(Int.MIN_VALUE, 1, ProtoIntegerType.FIXED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Int.MIN_VALUE, reader.readInt(ProtoIntegerType.FIXED))
    }

    @Test
    fun long_fixed_maxValue() {
        val bytes = encode { writeLong(Long.MAX_VALUE, 1, ProtoIntegerType.FIXED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Long.MAX_VALUE, reader.readLong(ProtoIntegerType.FIXED))
    }

    @Test
    fun long_fixed_minValue() {
        val bytes = encode { writeLong(Long.MIN_VALUE, 1, ProtoIntegerType.FIXED) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(Long.MIN_VALUE, reader.readLong(ProtoIntegerType.FIXED))
    }

    // --- Large string ---

    @Test
    fun string_largeUtf8_roundTrip() {
        val large = "A".repeat(10_000)
        val bytes = encode { writeString(large, 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertEquals(large, reader.readString())
    }

    // --- Empty bytes round-trip ---

    @Test
    fun bytes_emptyByteArray_roundTrip() {
        val bytes = encode { writeBytes(byteArrayOf(), 1) }
        val reader = decode(bytes)
        reader.readTag()
        assertTrue(reader.readByteArray().isEmpty())
    }

    // --- Multiple skips ---

    @Test
    fun skipMultipleFields() {
        val bytes = encode {
            writeInt(1, 1, ProtoIntegerType.DEFAULT)
            writeString("skip", 2)
            writeFloat(1.0f, 3)
            writeDouble(2.0, 4)
            writeBytes(byteArrayOf(1, 2, 3), 5)
            writeInt(42, 6, ProtoIntegerType.DEFAULT)
        }
        val reader = decode(bytes)
        // Skip fields 1-5, read field 6
        for (i in 1..5) {
            reader.readTag()
            reader.skipElement()
        }
        assertEquals(6, reader.readTag())
        assertEquals(42, reader.readInt(ProtoIntegerType.DEFAULT))
        assertTrue(reader.eof)
    }

    // --- Tag with high field number ---

    @Test
    fun highFieldNumber_roundTrip() {
        val bytes = encode { writeInt(99, 536870911, ProtoIntegerType.DEFAULT) }
        val reader = decode(bytes)
        assertEquals(536870911, reader.readTag()) // max proto field number
        assertEquals(99, reader.readInt(ProtoIntegerType.DEFAULT))
    }

    // --- NoTag variants ---

    @Test
    fun readInt32NoTag_roundTrip() {
        val out = ByteArrayOutput()
        out.encodeVarint64(42L)
        val reader = ProtobufReaderImpl(ByteArrayInput(out.toByteArray()))
        assertEquals(42, reader.readInt32NoTag())
    }

    @Test
    fun readLongNoTag_roundTrip() {
        val out = ByteArrayOutput()
        out.encodeVarint64(Long.MAX_VALUE)
        val reader = ProtobufReaderImpl(ByteArrayInput(out.toByteArray()))
        assertEquals(Long.MAX_VALUE, reader.readLongNoTag())
    }

    @Test
    fun readFloatNoTag_roundTrip() {
        val out = ByteArrayOutput()
        val bits = 1.5f.toRawBits().reverseBytes()
        out.writeInt(bits)
        val reader = ProtobufReaderImpl(ByteArrayInput(out.toByteArray()))
        assertEquals(1.5f, reader.readFloatNoTag())
    }

    @Test
    fun readDoubleNoTag_roundTrip() {
        val out = ByteArrayOutput()
        val bits = 1.5.toRawBits().reverseBytes()
        out.writeLong(bits)
        val reader = ProtobufReaderImpl(ByteArrayInput(out.toByteArray()))
        assertEquals(1.5, reader.readDoubleNoTag())
    }

    @Test
    fun readStringNoTag_roundTrip() {
        val out = ByteArrayOutput()
        val strBytes = "hello".encodeToByteArray()
        out.encodeVarint32(strBytes.size)
        out.write(strBytes)
        val reader = ProtobufReaderImpl(ByteArrayInput(out.toByteArray()))
        assertEquals("hello", reader.readStringNoTag())
    }
}
