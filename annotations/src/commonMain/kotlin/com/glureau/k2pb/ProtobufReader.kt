package com.glureau.k2pb

interface ProtobufReader {
    var currentId: Int
    var pushBack: Boolean
    var pushBackHeader: Int
    val eof: Boolean

    fun readTag(): Int
    fun updateIdAndType(header: Int): Int
    fun pushBackTag()
    fun skipElement()
    fun readByteArray(): ByteArray
    fun skipSizeDelimited()
    fun readByteArrayNoTag(): ByteArray
    //fun objectInput(): ByteArrayInput
    //fun objectTaglessInput(): ByteArrayInput
    fun readInt(format: ProtoIntegerType): Int
    fun readInt32NoTag(): Int
    fun readLong(format: ProtoIntegerType): Long
    fun readLongNoTag(): Long
    fun readFloat(): Float
    fun readFloatNoTag(): Float
    fun readIntLittleEndian(): Int
    fun readLongLittleEndian(): Long
    fun readDouble(): Double
    fun readDoubleNoTag(): Double
    fun readString(): String
    fun readStringNoTag(): String
    fun checkLength(length: Int)
    fun decode32(format: ProtoIntegerType = ProtoIntegerType.DEFAULT): Int
    fun decode64(format: ProtoIntegerType = ProtoIntegerType.DEFAULT): Long

    /**
     *  Source for all varint operations:
     *  https://github.com/addthis/stream-lib/blob/master/src/main/java/com/clearspring/analytics/util/Varint.java
     */
    //fun decodeSignedVarintInt(input: ByteArrayInput): Int
    //fun decodeSignedVarintLong(input: ByteArrayInput): Long
}