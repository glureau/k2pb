package com.glureau.sample

import kotlinx.serialization.Serializable

@Serializable
class StandardClass(
    val eventUUID: String,
    val bytes: ByteArray,
) {

    val foo: String = "hello"

    override fun toString(): String {
        return "StandardClass(eventUUID='$eventUUID', bytes=${bytes.contentToString()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StandardClass

        if (eventUUID != other.eventUUID) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = eventUUID.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }

}