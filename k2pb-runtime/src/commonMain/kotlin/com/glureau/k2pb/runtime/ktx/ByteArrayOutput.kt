package com.glureau.k2pb.runtime.ktx

public class ByteArrayOutput() {

    private companion object {
        /*
         * Map number of leading zeroes -> varint size
         * See the explanation in this blogpost: https://richardstartin.github.io/posts/dont-use-protobuf-for-telemetry
         */
        private val VAR_INT_LENGTHS = IntArray(65) {
            (63 - it) / 7
        }
    }

    private var array: ByteArray = ByteArray(32)
    private var position: Int = 0

    private fun ensureCapacity(elementsToAppend: Int) {
        if (position + elementsToAppend <= array.size) {
            return
        }
        val newArray = ByteArray((position + elementsToAppend).takeHighestOneBit() shl 1)
        array.copyInto(newArray)
        array = newArray
    }

    public fun size(): Int {
        return position
    }

    public fun toByteArray(): ByteArray {
        val newArray = ByteArray(position)
        array.copyInto(newArray, startIndex = 0, endIndex = this.position)
        return newArray
    }

    public fun write(buffer: ByteArray) {
        val count = buffer.size
        if (count == 0) {
            return
        }

        ensureCapacity(count)
        buffer.copyInto(
            destination = array,
            destinationOffset = this.position,
            startIndex = 0,
            endIndex = count
        )
        this.position += count
    }

    public fun write(output: ByteArrayOutput) {
        val count = output.size()
        ensureCapacity(count)
        output.array.copyInto(
            destination = array,
            destinationOffset = this.position,
            startIndex = 0,
            endIndex = count
        )
        this.position += count
    }

    public fun writeInt(intValue: Int) {
        ensureCapacity(4)
        for (i in 3 downTo 0) {
            array[position++] = (intValue shr i * 8).toByte()
        }
    }

    public fun writeLong(longValue: Long) {
        ensureCapacity(8)
        for (i in 7 downTo 0) {
            array[position++] = (longValue shr i * 8).toByte()
        }
    }

    public fun encodeVarint32(value: Int) {
        // Fast-path: unrolled loop for single byte
        ensureCapacity(5)
        if (value and 0x7F.inv() == 0) {
            array[position++] = value.toByte()
            return
        }
        val length = varIntLength(value.toLong())
        encodeVarint(value.toLong(), length)
    }

    public fun encodeVarint64(value: Long) {
        val length = varIntLength(value)
        ensureCapacity(length + 1)
        encodeVarint(value, length)
    }

    private fun encodeVarint(value: Long, length: Int) {
        var current = value
        for (i in 0 until length) {
            array[position + i] = ((current and 0x7F) or 0x80).toByte()
            current = current ushr 7
        }
        array[position + length] = current.toByte()
        position += length + 1
    }

    private fun varIntLength(value: Long): Int {
        return VAR_INT_LENGTHS[value.countLeadingZeroBits()]
    }
}
