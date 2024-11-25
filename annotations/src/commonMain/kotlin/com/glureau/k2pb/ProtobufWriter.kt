package com.glureau.k2pb

public interface ProtobufWriter {
    public fun writeBytes(bytes: ByteArray, tag: Int)
    public fun writeBytes(bytes: ByteArray)
    public fun writeInt(value: Int, tag: Int, format: ProtoIntegerType)
    public fun writeInt(value: Int)
    public fun writeLong(value: Long, tag: Int, format: ProtoIntegerType)
    public fun writeLong(value: Long)
    public fun writeString(value: String, tag: Int)
    public fun writeString(value: String)
    public fun writeDouble(value: Double, tag: Int)
    public fun writeDouble(value: Double)
    public fun writeFloat(value: Float, tag: Int)
    public fun writeFloat(value: Float)
}

