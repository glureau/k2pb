package com.glureau.k2pb

interface ProtobufWriter {
    fun writeBytes(bytes: ByteArray, tag: Int)
    fun writeBytes(bytes: ByteArray)
    fun writeInt(value: Int, tag: Int, format: ProtoIntegerType)
    fun writeInt(value: Int)
    fun writeLong(value: Long, tag: Int, format: ProtoIntegerType)
    fun writeLong(value: Long)
    fun writeString(value: String, tag: Int)
    fun writeString(value: String)
    fun writeDouble(value: Double, tag: Int)
    fun writeDouble(value: Double)
    fun writeFloat(value: Float, tag: Int)
    fun writeFloat(value: Float)
}

