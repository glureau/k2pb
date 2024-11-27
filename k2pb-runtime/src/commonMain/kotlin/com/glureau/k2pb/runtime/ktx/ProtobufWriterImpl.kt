/*
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package com.glureau.k2pb.runtime.ktx

import com.glureau.k2pb.ProtoIntegerType
import com.glureau.k2pb.ProtobufWriter

internal class ProtobufWriterImpl(private val out: ByteArrayOutput) : ProtobufWriter {
    override fun writeBytes(bytes: ByteArray, tag: Int) {
        out.encode32(ProtoWireType.SIZE_DELIMITED.wireIntWithTag(tag))
        writeBytes(bytes)
    }

    override fun writeBytes(bytes: ByteArray) {
        out.encode32(bytes.size)
        out.write(bytes)
    }

    fun writeOutput(output: ByteArrayOutput, tag: Int) {
        out.encode32(ProtoWireType.SIZE_DELIMITED.wireIntWithTag(tag))
        writeOutput(output)
    }

    fun writeOutput(output: ByteArrayOutput) {
        out.encode32(output.size())
        out.write(output)
    }

    override fun writeInt(value: Int, tag: Int, format: ProtoIntegerType) {
        val wireType = if (format == ProtoIntegerType.FIXED) ProtoWireType.i32 else ProtoWireType.VARINT
        out.encode32(wireType.wireIntWithTag(tag))
        out.encode32(value, format)
    }

    override fun writeInt(value: Int) {
        out.encode32(value)
    }

    override fun writeLong(value: Long, tag: Int, format: ProtoIntegerType) {
        val wireType = if (format == ProtoIntegerType.FIXED) ProtoWireType.i64 else ProtoWireType.VARINT
        out.encode32(wireType.wireIntWithTag(tag))
        out.encode64(value, format)
    }

    override fun writeLong(value: Long) {
        out.encode64(value)
    }

    override fun writeString(value: String, tag: Int) {
        val bytes = value.encodeToByteArray() // TODO: this Ktx encoding can replace some chars, UTF-8 limitation
        writeBytes(bytes, tag)
    }

    override fun writeString(value: String) {
        val bytes = value.encodeToByteArray()
        writeBytes(bytes)
    }

    override fun writeDouble(value: Double, tag: Int) {
        out.encode32(ProtoWireType.i64.wireIntWithTag(tag))
        out.writeLong(value.reverseBytes())
    }

    override fun writeDouble(value: Double) {
        out.writeLong(value.reverseBytes())
    }

    override fun writeFloat(value: Float, tag: Int) {
        out.encode32(ProtoWireType.i32.wireIntWithTag(tag))
        out.writeInt(value.reverseBytes())
    }

    override fun writeFloat(value: Float) {
        out.writeInt(value.reverseBytes())
    }

    private fun ByteArrayOutput.encode32(
        number: Int,
        format: ProtoIntegerType = ProtoIntegerType.DEFAULT
    ) {
        when (format) {
            ProtoIntegerType.FIXED -> out.writeInt(number.reverseBytes())
            ProtoIntegerType.DEFAULT -> encodeVarint64(number.toLong())
            ProtoIntegerType.SIGNED -> encodeVarint32(((number shl 1) xor (number shr 31)))
        }
    }

    private fun ByteArrayOutput.encode64(number: Long, format: ProtoIntegerType = ProtoIntegerType.DEFAULT) {
        when (format) {
            ProtoIntegerType.FIXED -> out.writeLong(number.reverseBytes())
            ProtoIntegerType.DEFAULT -> encodeVarint64(number)
            ProtoIntegerType.SIGNED -> encodeVarint64((number shl 1) xor (number shr 63))
        }
    }

    private fun Float.reverseBytes(): Int = toRawBits().reverseBytes()

    private fun Double.reverseBytes(): Long = toRawBits().reverseBytes()
}
