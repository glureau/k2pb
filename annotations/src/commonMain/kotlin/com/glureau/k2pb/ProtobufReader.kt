package com.glureau.k2pb

public interface ProtobufReader {
    public var currentId: Int
    public var pushBack: Boolean
    public var pushBackHeader: Int
    public val eof: Boolean

    public fun readTag(): Int
    public fun updateIdAndType(header: Int): Int
    public fun pushBackTag()
    public fun skipElement()
    public fun readByteArray(): ByteArray
    public fun skipSizeDelimited()
    public fun readByteArrayNoTag(): ByteArray
    //fun objectInput(): ByteArrayInput
    //fun objectTaglessInput(): ByteArrayInput
    public fun readInt(format: ProtoIntegerType): Int
    public fun readInt32NoTag(): Int
    public fun readLong(format: ProtoIntegerType): Long
    public fun readLongNoTag(): Long
    public fun readFloat(): Float
    public fun readFloatNoTag(): Float
    public fun readIntLittleEndian(): Int
    public fun readLongLittleEndian(): Long
    public fun readDouble(): Double
    public fun readDoubleNoTag(): Double
    public fun readString(): String
    public fun readStringNoTag(): String
    public fun checkLength(length: Int)
    public fun decode32(format: ProtoIntegerType = ProtoIntegerType.DEFAULT): Int
    public fun decode64(format: ProtoIntegerType = ProtoIntegerType.DEFAULT): Long

    /**
     *  Source for all varint operations:
     *  https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/util/Varint.java
     */
    //fun decodeSignedVarintInt(input: ByteArrayInput): Int
    //fun decodeSignedVarintLong(input: ByteArrayInput): Long
}