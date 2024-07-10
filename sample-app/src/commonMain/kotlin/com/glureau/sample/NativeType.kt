package com.glureau.sample

import kotlinx.serialization.Serializable

@Serializable
data class NativeTypeEvent(
    // Using same mapping than in Kotlinx Serialization:
    // https://github.com/Kotlin/kotlinx.serialization/blob/master/formats/protobuf/commonMain/src/kotlinx/serialization/protobuf/internal/ProtobufEncoding.kt
    val integer: Int, // int32
    val long: Long, // int64
    val float: Float, // float
    val double: Double, // float
    val string: String, // string
    val short: Short, // int32
    val char: Char, // int32
    val boolean: Boolean, // bool
    val byte: Byte, // int32
    val byteArray: ByteArray, // bytes
) : EventInterface {

    // ByteArray requires to generate equals & hashcode, as data class doesn't compare ByteArray content

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NativeTypeEvent

        if (integer != other.integer) return false
        if (long != other.long) return false
        if (float != other.float) return false
        if (double != other.double) return false
        if (string != other.string) return false
        if (short != other.short) return false
        if (char != other.char) return false
        if (boolean != other.boolean) return false
        if (byte != other.byte) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = integer
        result = 31 * result + long.hashCode()
        result = 31 * result + float.hashCode()
        result = 31 * result + double.hashCode()
        result = 31 * result + string.hashCode()
        result = 31 * result + short
        result = 31 * result + char.hashCode()
        result = 31 * result + boolean.hashCode()
        result = 31 * result + byte
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}
