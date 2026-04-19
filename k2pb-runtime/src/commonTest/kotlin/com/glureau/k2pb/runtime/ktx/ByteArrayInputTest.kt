package com.glureau.k2pb.runtime.ktx

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ByteArrayInputTest {

    @Test
    fun readVarint32_singleByte() {
        val input = ByteArrayInput(byteArrayOf(0x00))
        assertEquals(0, input.readVarint32())
    }

    @Test
    fun readVarint32_singleByteMax() {
        val input = ByteArrayInput(byteArrayOf(0x7F))
        assertEquals(127, input.readVarint32())
    }

    @Test
    fun readVarint32_twoByte() {
        val input = ByteArrayInput(byteArrayOf(0x80.toByte(), 0x01))
        assertEquals(128, input.readVarint32())
    }

    @Test
    fun readVarint32_fiveByte_maxValue() {
        // Encode Int.MAX_VALUE as varint, then decode
        val out = ByteArrayOutput()
        out.encodeVarint32(Int.MAX_VALUE)
        val input = ByteArrayInput(out.toByteArray())
        assertEquals(Int.MAX_VALUE, input.readVarint32())
    }

    @Test
    fun readVarint32_eof_throws() {
        val input = ByteArrayInput(byteArrayOf())
        assertFailsWith<SerializationException> {
            input.readVarint32()
        }
    }

    @Test
    fun readVarint64_singleByte() {
        val input = ByteArrayInput(byteArrayOf(0x01))
        assertEquals(1L, input.readVarint64(false))
    }

    @Test
    fun readVarint64_eof_allowed() {
        val input = ByteArrayInput(byteArrayOf())
        assertEquals(-1L, input.readVarint64(true))
    }

    @Test
    fun readVarint64_eof_notAllowed() {
        val input = ByteArrayInput(byteArrayOf())
        assertFailsWith<SerializationException> {
            input.readVarint64(false)
        }
    }

    @Test
    fun readVarint64_maxValue_roundTrip() {
        val out = ByteArrayOutput()
        out.encodeVarint64(Long.MAX_VALUE)
        val input = ByteArrayInput(out.toByteArray())
        assertEquals(Long.MAX_VALUE, input.readVarint64(false))
    }

    @Test
    fun readVarint64_negativeOne_roundTrip() {
        val out = ByteArrayOutput()
        out.encodeVarint64(-1L)
        val input = ByteArrayInput(out.toByteArray())
        assertEquals(-1L, input.readVarint64(false))
    }

    @Test
    fun readExactNBytes_success() {
        val input = ByteArrayInput(byteArrayOf(1, 2, 3, 4, 5))
        val result = input.readExactNBytes(3)
        assertTrue(result.contentEquals(byteArrayOf(1, 2, 3)))
        assertEquals(2, input.availableBytes)
    }

    @Test
    fun readExactNBytes_insufficientBytes() {
        val input = ByteArrayInput(byteArrayOf(1, 2))
        assertFailsWith<SerializationException> {
            input.readExactNBytes(5)
        }
    }

    @Test
    fun skipExactNBytes_success() {
        val input = ByteArrayInput(byteArrayOf(1, 2, 3, 4, 5))
        input.skipExactNBytes(3)
        assertEquals(2, input.availableBytes)
    }

    @Test
    fun skipExactNBytes_insufficientBytes() {
        val input = ByteArrayInput(byteArrayOf(1, 2))
        assertFailsWith<SerializationException> {
            input.skipExactNBytes(5)
        }
    }

    @Test
    fun readString_basic() {
        val text = "hello"
        val bytes = text.encodeToByteArray()
        val input = ByteArrayInput(bytes)
        assertEquals("hello", input.readString(bytes.size))
    }

    @Test
    fun slice_isolatesSubRange() {
        val input = ByteArrayInput(byteArrayOf(10, 20, 30, 40, 50))
        val sliced = input.slice(3)
        assertEquals(3, sliced.availableBytes)
        assertEquals(2, input.availableBytes) // parent advanced past the slice

        val bytes = sliced.readExactNBytes(3)
        assertTrue(bytes.contentEquals(byteArrayOf(10, 20, 30)))
    }

    @Test
    fun slice_insufficientBytes() {
        val input = ByteArrayInput(byteArrayOf(1, 2))
        assertFailsWith<SerializationException> {
            input.slice(5)
        }
    }

    @Test
    fun read_returnsMinusOneAtEof() {
        val input = ByteArrayInput(byteArrayOf(42))
        assertEquals(42, input.read())
        assertEquals(-1, input.read())
    }

    @Test
    fun availableBytes_tracksPosition() {
        val input = ByteArrayInput(byteArrayOf(1, 2, 3))
        assertEquals(3, input.availableBytes)
        input.read()
        assertEquals(2, input.availableBytes)
        input.read()
        assertEquals(1, input.availableBytes)
        input.read()
        assertEquals(0, input.availableBytes)
    }

    @Test
    fun varint32_roundTrip_variousValues() {
        val values = listOf(0, 1, 127, 128, 255, 256, 16383, 16384, Int.MAX_VALUE)
        for (v in values) {
            val out = ByteArrayOutput()
            out.encodeVarint32(v)
            val input = ByteArrayInput(out.toByteArray())
            assertEquals(v, input.readVarint32(), "Round-trip failed for $v")
        }
    }

    @Test
    fun varint64_roundTrip_variousValues() {
        val values = listOf(0L, 1L, 127L, 128L, 16383L, 16384L, Long.MAX_VALUE, -1L, Long.MIN_VALUE)
        for (v in values) {
            val out = ByteArrayOutput()
            out.encodeVarint64(v)
            val input = ByteArrayInput(out.toByteArray())
            assertEquals(v, input.readVarint64(false), "Round-trip failed for $v")
        }
    }
}
