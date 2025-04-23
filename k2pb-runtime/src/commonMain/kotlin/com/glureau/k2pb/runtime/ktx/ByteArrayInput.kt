package com.glureau.k2pb.runtime.ktx

internal class ByteArrayInput(private var array: ByteArray, private val endIndex: Int = array.size) {
    private var position: Int = 0
    val availableBytes: Int get() = endIndex - position

    fun slice(size: Int): ByteArrayInput {
        ensureEnoughBytes(size)
        val result = ByteArrayInput(array, position + size)
        result.position = position
        position += size
        return result
    }

    fun read(): Int {
        return if (position < endIndex) array[position++].toInt() and 0xFF else -1
    }

    fun readExactNBytes(bytesCount: Int): ByteArray {
        ensureEnoughBytes(bytesCount)
        val b = ByteArray(bytesCount)
        val length = b.size
        // Are there any bytes available?
        val copied = if (endIndex - position < length) endIndex - position else length
        array.copyInto(destination = b, destinationOffset = 0, startIndex = position, endIndex = position + copied)
        position += copied
        return b
    }


    fun skipExactNBytes(bytesCount: Int) {
        ensureEnoughBytes(bytesCount)
        position += bytesCount
    }

    private fun ensureEnoughBytes(bytesCount: Int) {
        if (bytesCount > availableBytes) {
            throw SerializationException("Unexpected EOF, available $availableBytes bytes, requested: $bytesCount")
        }
    }

    fun readString(length: Int): String {
        val result = array.decodeToString(position, position + length)
        position += length
        return result
    }

    fun readVarint32(): Int {
        if (position == endIndex) {
            eof()
        }

        // Fast-path: unrolled loop for single and two byte values
        var currentPosition = position
        var result = array[currentPosition++].toInt()
        if (result >= 0) {
            position = currentPosition
            return result
        } else if (endIndex - position > 1) {
            result = result xor (array[currentPosition++].toInt() shl 7)
            if (result < 0) {
                position = currentPosition
                return result xor (0.inv() shl 7)
            }
        }

        return readVarint32SlowPath()
    }

    fun readVarint64(eofAllowed: Boolean): Long {
        if (position == endIndex) {
            if (eofAllowed) return -1
            else eof()
        }

        // Fast-path: single and two byte values
        var currentPosition = position
        var result = array[currentPosition++].toLong()
        if (result >= 0) {
            position = currentPosition
            return result
        } else if (endIndex - position > 1) {
            result = result xor (array[currentPosition++].toLong() shl 7)
            if (result < 0) {
                position = currentPosition
                return result xor (0L.inv() shl 7)
            }
        }

        return readVarint64SlowPath()
    }

    private fun eof() {
        throw SerializationException("Unexpected EOF")
    }

    private fun readVarint64SlowPath(): Long {
        var result = 0L
        var shift = 0
        while (shift < 64) {
            val byte = read()
            result = result or ((byte and 0x7F).toLong() shl shift)
            if (byte and 0x80 == 0) {
                return result
            }
            shift += 7
        }
        throw SerializationException("Input stream is malformed: Varint too long (exceeded 64 bits)")
    }

    private fun readVarint32SlowPath(): Int {
        var result = 0
        var shift = 0
        while (shift < 32) {
            val byte = read()
            result = result or ((byte and 0x7F) shl shift)
            if (byte and 0x80 == 0) {
                return result
            }
            shift += 7
        }
        throw SerializationException("Input stream is malformed: Varint too long (exceeded 32 bits)")
    }
}
