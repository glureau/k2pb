package com.glureau.k2pb.runtime.ktx

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ByteArrayOutputTest {

    @Test
    fun encodeVarint32_singleByte() {
        val out = ByteArrayOutput()
        out.encodeVarint32(0)
        assertEquals(1, out.size())
        assertEquals(0x00.toByte(), out.toByteArray()[0])
    }

    @Test
    fun encodeVarint32_singleByteMax() {
        val out = ByteArrayOutput()
        out.encodeVarint32(127)
        assertEquals(1, out.size())
        assertEquals(0x7F.toByte(), out.toByteArray()[0])
    }

    @Test
    fun encodeVarint32_twoByte() {
        val out = ByteArrayOutput()
        out.encodeVarint32(128) // 0x80
        val bytes = out.toByteArray()
        assertEquals(2, bytes.size)
        assertEquals(0x80.toByte(), bytes[0])
        assertEquals(0x01.toByte(), bytes[1])
    }

    @Test
    fun encodeVarint32_maxValue() {
        val out = ByteArrayOutput()
        out.encodeVarint32(Int.MAX_VALUE)
        val bytes = out.toByteArray()
        // Int.MAX_VALUE = 2147483647 requires 5 varint bytes
        assertEquals(5, bytes.size)
    }

    @Test
    fun encodeVarint32_negativeOne() {
        // Negative int32 values are sign-extended to 64 bits per protobuf spec,
        // requiring up to 10 varint bytes.
        val out = ByteArrayOutput()
        out.encodeVarint32(-1)
        val bytes = out.toByteArray()
        assertEquals(10, bytes.size)
        // All continuation bytes should be 0xFF, last byte should be 0x01
        for (i in 0 until 9) {
            assertEquals(0xFF.toByte(), bytes[i], "byte $i should be 0xFF")
        }
        assertEquals(0x01.toByte(), bytes[9])
    }

    @Test
    fun encodeVarint32_negativeMinValue() {
        val out = ByteArrayOutput()
        out.encodeVarint32(Int.MIN_VALUE)
        val bytes = out.toByteArray()
        assertEquals(10, bytes.size)
    }

    @Test
    fun encodeVarint64_zero() {
        val out = ByteArrayOutput()
        out.encodeVarint64(0L)
        assertEquals(1, out.size())
        assertEquals(0x00.toByte(), out.toByteArray()[0])
    }

    @Test
    fun encodeVarint64_maxValue() {
        val out = ByteArrayOutput()
        out.encodeVarint64(Long.MAX_VALUE)
        val bytes = out.toByteArray()
        assertEquals(9, bytes.size)
    }

    @Test
    fun encodeVarint64_negativeOne() {
        val out = ByteArrayOutput()
        out.encodeVarint64(-1L)
        val bytes = out.toByteArray()
        assertEquals(10, bytes.size)
    }

    @Test
    fun encodeVarint64_negativeMinValue() {
        val out = ByteArrayOutput()
        out.encodeVarint64(Long.MIN_VALUE)
        val bytes = out.toByteArray()
        assertEquals(10, bytes.size)
    }

    @Test
    fun writeInt_bigEndian() {
        val out = ByteArrayOutput()
        out.writeInt(0x01020304)
        val bytes = out.toByteArray()
        assertEquals(4, bytes.size)
        assertEquals(0x01.toByte(), bytes[0])
        assertEquals(0x02.toByte(), bytes[1])
        assertEquals(0x03.toByte(), bytes[2])
        assertEquals(0x04.toByte(), bytes[3])
    }

    @Test
    fun writeLong_bigEndian() {
        val out = ByteArrayOutput()
        out.writeLong(0x0102030405060708L)
        val bytes = out.toByteArray()
        assertEquals(8, bytes.size)
        assertEquals(0x01.toByte(), bytes[0])
        assertEquals(0x08.toByte(), bytes[7])
    }

    @Test
    fun writeBytes_empty() {
        val out = ByteArrayOutput()
        out.write(byteArrayOf())
        assertEquals(0, out.size())
    }

    @Test
    fun writeBytes_data() {
        val out = ByteArrayOutput()
        out.write(byteArrayOf(1, 2, 3))
        assertEquals(3, out.size())
        val bytes = out.toByteArray()
        assertEquals(1.toByte(), bytes[0])
        assertEquals(3.toByte(), bytes[2])
    }

    @Test
    fun capacityGrowth_largeWrite() {
        val out = ByteArrayOutput()
        // Write more than initial capacity (32 bytes)
        val data = ByteArray(100) { it.toByte() }
        out.write(data)
        assertEquals(100, out.size())
        val result = out.toByteArray()
        assertTrue(result.contentEquals(data))
    }

    @Test
    fun multipleWrites() {
        val out = ByteArrayOutput()
        out.encodeVarint32(1)
        out.encodeVarint32(300)
        out.encodeVarint64(Long.MAX_VALUE)
        // Should not throw; verify total size is reasonable
        assertTrue(out.size() > 0)
    }

    @Test
    fun writeOutput_copiesFromAnother() {
        val inner = ByteArrayOutput()
        inner.write(byteArrayOf(10, 20, 30))

        val outer = ByteArrayOutput()
        outer.write(inner)
        assertEquals(3, outer.size())
        assertTrue(outer.toByteArray().contentEquals(byteArrayOf(10, 20, 30)))
    }
}
