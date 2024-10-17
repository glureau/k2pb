package com.glureau.k2pb.runtime

class K2PB {
    inline fun <reified T> encodeToByteArray(any: T): ByteArray {
        T::class
        return byteArrayOf(0)
    }

    inline fun <reified T> decodeFromByteArray(encoded: ByteArray?): T {
        T::class
        TODO()
    }
}