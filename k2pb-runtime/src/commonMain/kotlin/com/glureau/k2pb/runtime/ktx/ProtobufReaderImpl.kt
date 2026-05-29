/*
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */
//@file:OptIn(ExperimentalSerializationApi::class)

package com.glureau.k2pb.runtime.ktx

import com.glureau.k2pb.ProtoIntegerType
import com.glureau.k2pb.ProtobufReader

//import kotlinx.serialization.*
//import kotlinx.serialization.protobuf.*
//import kotlin.jvm.*

internal class ProtobufReaderImpl(private val input: ByteArrayInput) : ProtobufReader {
    override var currentId = -1
    private var currentType = ProtoWireType.INVALID
    override var pushBack = false
    override var pushBackHeader = 0

    override val eof
        get() = !pushBack && input.availableBytes == 0

    override fun readTag(): Int {
        if (pushBack) {
            pushBack = false
            val previousHeader = (currentId shl 3) or currentType.typeId
            return updateIdAndType(pushBackHeader).also {
                pushBackHeader = previousHeader
            }
        }
        // Header to use when pushed back is the old id/type
        pushBackHeader = (currentId shl 3) or currentType.typeId

        val header = input.readVarint64(true).toInt()
        return updateIdAndType(header)
    }

    override fun updateIdAndType(header: Int): Int {
        return if (header == -1) {
            currentId = -1
            currentType = ProtoWireType.INVALID
            -1
        } else {
            currentId = header ushr 3
            currentType = ProtoWireType.from(header and 0b111)
            currentId
        }
    }

    override fun pushBackTag() {
        pushBack = true

        val nextHeader = (currentId shl 3) or currentType.typeId
        updateIdAndType(pushBackHeader)
        pushBackHeader = nextHeader
    }

    override fun skipElement() {
        when (currentType) {
            ProtoWireType.VARINT -> readInt(ProtoIntegerType.DEFAULT)
            ProtoWireType.I64 -> readLong(ProtoIntegerType.FIXED)
            ProtoWireType.SIZE_DELIMITED -> skipSizeDelimited()
            ProtoWireType.I32 -> readInt(ProtoIntegerType.FIXED)
            else -> throw SerializationException("Unsupported start group or end group wire type: $currentType")
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun assertWireType(expected: ProtoWireType) {
        if (currentType != expected) throw SerializationException("Expected wire type $expected, but found $currentType")
    }

    override fun readByteArray(): ByteArray {
        assertWireType(ProtoWireType.SIZE_DELIMITED)
        return readByteArrayNoTag()
    }

    override fun skipSizeDelimited() {
        assertWireType(ProtoWireType.SIZE_DELIMITED)
        val length = decode32()
        checkLength(length)
        input.skipExactNBytes(length)
    }

    override fun readByteArrayNoTag(): ByteArray {
        val length = decode32()
        checkLength(length)
        return input.readExactNBytes(length)
    }

    override fun readSubReader(): ProtobufReaderImpl {
        assertWireType(ProtoWireType.SIZE_DELIMITED)
        val length = decode32()
        checkLength(length)
        return ProtobufReaderImpl(input.slice(length))
    }

    override fun readInt(format: ProtoIntegerType): Int {
        val wireType = if (format == ProtoIntegerType.FIXED) ProtoWireType.I32 else ProtoWireType.VARINT
        assertWireType(wireType)
        return decode32(format)
    }

    override fun readInt32NoTag(): Int = decode32()

    override fun readLong(format: ProtoIntegerType): Long {
        val wireType = if (format == ProtoIntegerType.FIXED) ProtoWireType.I64 else ProtoWireType.VARINT
        assertWireType(wireType)
        return decode64(format)
    }

    override fun readLongNoTag(): Long = decode64(ProtoIntegerType.DEFAULT)

    override fun readFloat(): Float {
        assertWireType(ProtoWireType.I32)
        return Float.fromBits(readIntLittleEndian())
    }

    override fun readFloatNoTag(): Float = Float.fromBits(readIntLittleEndian())

    override fun readIntLittleEndian(): Int = input.readIntLittleEndian()

    override fun readLongLittleEndian(): Long = input.readLongLittleEndian()

    override fun readDouble(): Double {
        assertWireType(ProtoWireType.I64)
        return Double.fromBits(readLongLittleEndian())
    }

    override fun readDoubleNoTag(): Double {
        return Double.fromBits(readLongLittleEndian())
    }

    override fun readString(): String {
        assertWireType(ProtoWireType.SIZE_DELIMITED)
        val length = decode32()
        checkLength(length)
        return input.readString(length)
    }

    override fun readStringNoTag(): String {
        val length = decode32()
        checkLength(length)
        return input.readString(length)
    }

    override fun checkLength(length: Int) {
        if (length < 0) {
            throw SerializationException("Unexpected negative length: $length")
        }
    }

    override fun decode32(format: ProtoIntegerType): Int = when (format) {
        ProtoIntegerType.DEFAULT -> input.readVarint64(false).toInt()
        ProtoIntegerType.SIGNED -> decodeSignedVarintInt(
            input
        )

        ProtoIntegerType.FIXED -> readIntLittleEndian()
    }

    override fun decode64(format: ProtoIntegerType): Long = when (format) {
        ProtoIntegerType.DEFAULT -> input.readVarint64(false)
        ProtoIntegerType.SIGNED -> decodeSignedVarintLong(
            input
        )

        ProtoIntegerType.FIXED -> readLongLittleEndian()
    }

    /**
     *  Source for all varint operations:
     *  https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/util/Varint.java
     */
    fun decodeSignedVarintInt(input: ByteArrayInput): Int {
        val raw = input.readVarint32()
        val temp = raw shl 31 shr 31 xor raw shr 1
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values.
        // Must re-flip the top bit if the original read value had it set.
        return temp xor (raw and (1 shl 31))
    }

    fun decodeSignedVarintLong(input: ByteArrayInput): Long {
        val raw = input.readVarint64(false)
        val temp = raw shl 63 shr 63 xor raw shr 1
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values
        // Must re-flip the top bit if the original read value had it set.
        return temp xor (raw and (1L shl 63))
    }
}
