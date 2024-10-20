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
            ProtoWireType.i64 -> readLong(ProtoIntegerType.FIXED)
            ProtoWireType.SIZE_DELIMITED -> skipSizeDelimited()
            ProtoWireType.i32 -> readInt(ProtoIntegerType.FIXED)
            else -> throw ProtobufDecodingException("Unsupported start group or end group wire type: $currentType")
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun assertWireType(expected: ProtoWireType) {
        if (currentType != expected) throw ProtobufDecodingException("Expected wire type $expected, but found $currentType")
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

    fun objectInput(): ByteArrayInput {
        assertWireType(ProtoWireType.SIZE_DELIMITED)
        return objectTaglessInput()
    }

    fun objectTaglessInput(): ByteArrayInput {
        val length = decode32()
        checkLength(length)
        return input.slice(length)
    }

    override fun readInt(format: ProtoIntegerType): Int {
        val wireType = if (format == ProtoIntegerType.FIXED) ProtoWireType.i32 else ProtoWireType.VARINT
        assertWireType(wireType)
        return decode32(format)
    }

    override fun readInt32NoTag(): Int = decode32()

    override fun readLong(format: ProtoIntegerType): Long {
        val wireType = if (format == ProtoIntegerType.FIXED) ProtoWireType.i64 else ProtoWireType.VARINT
        assertWireType(wireType)
        return decode64(format)
    }

    override fun readLongNoTag(): Long = decode64(ProtoIntegerType.DEFAULT)

    override fun readFloat(): Float {
        assertWireType(ProtoWireType.i32)
        return Float.fromBits(readIntLittleEndian())
    }

    override fun readFloatNoTag(): Float = Float.fromBits(readIntLittleEndian())

    override fun readIntLittleEndian(): Int {
        // TODO this could be optimized by extracting method to the IS
        var result = 0
        for (i in 0..3) {
            val byte = input.read() and 0x000000FF
            result = result or (byte shl (i * 8))
        }
        return result
    }

    override fun readLongLittleEndian(): Long {
        // TODO this could be optimized by extracting method to the IS
        var result = 0L
        for (i in 0..7) {
            val byte = (input.read() and 0x000000FF).toLong()
            result = result or (byte shl (i * 8))
        }
        return result
    }

    override fun readDouble(): Double {
        assertWireType(ProtoWireType.i64)
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
            throw ProtobufDecodingException("Unexpected negative length: $length")
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
