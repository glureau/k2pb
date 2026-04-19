package com.glureau.k2pb.runtime.ktx

import kotlin.test.Test
import kotlin.test.assertEquals

class ProtoWireTypeTest {

    @Test
    fun from_varint() {
        assertEquals(ProtoWireType.VARINT, ProtoWireType.from(0))
    }

    @Test
    fun from_i64() {
        assertEquals(ProtoWireType.I64, ProtoWireType.from(1))
    }

    @Test
    fun from_sizeDelimited() {
        assertEquals(ProtoWireType.SIZE_DELIMITED, ProtoWireType.from(2))
    }

    @Test
    fun from_i32() {
        assertEquals(ProtoWireType.I32, ProtoWireType.from(5))
    }

    @Test
    fun from_unknown_returnsInvalid() {
        assertEquals(ProtoWireType.INVALID, ProtoWireType.from(3))
        assertEquals(ProtoWireType.INVALID, ProtoWireType.from(4))
        assertEquals(ProtoWireType.INVALID, ProtoWireType.from(6))
        assertEquals(ProtoWireType.INVALID, ProtoWireType.from(7))
        assertEquals(ProtoWireType.INVALID, ProtoWireType.from(99))
    }

    @Test
    fun wireIntWithTag_varint() {
        // tag=1, wireType=VARINT(0) -> (1 shl 3) or 0 = 8
        assertEquals(8, ProtoWireType.VARINT.wireIntWithTag(1))
    }

    @Test
    fun wireIntWithTag_i64() {
        // tag=1, wireType=I64(1) -> (1 shl 3) or 1 = 9
        assertEquals(9, ProtoWireType.I64.wireIntWithTag(1))
    }

    @Test
    fun wireIntWithTag_sizeDelimited() {
        // tag=2, wireType=SIZE_DELIMITED(2) -> (2 shl 3) or 2 = 18
        assertEquals(18, ProtoWireType.SIZE_DELIMITED.wireIntWithTag(2))
    }

    @Test
    fun wireIntWithTag_i32() {
        // tag=3, wireType=I32(5) -> (3 shl 3) or 5 = 29
        assertEquals(29, ProtoWireType.I32.wireIntWithTag(3))
    }

    @Test
    fun wireIntWithTag_largeTag() {
        // tag=100 -> (100 shl 3) or 0 = 800
        assertEquals(800, ProtoWireType.VARINT.wireIntWithTag(100))
    }

    @Test
    fun toString_format() {
        assertEquals("VARINT(0)", ProtoWireType.VARINT.toString())
        assertEquals("I64(1)", ProtoWireType.I64.toString())
        assertEquals("SIZE_DELIMITED(2)", ProtoWireType.SIZE_DELIMITED.toString())
        assertEquals("I32(5)", ProtoWireType.I32.toString())
        assertEquals("INVALID(-1)", ProtoWireType.INVALID.toString())
    }
}
