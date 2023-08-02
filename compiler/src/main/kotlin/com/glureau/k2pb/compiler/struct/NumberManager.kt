package com.glureau.k2pb.compiler.struct

// 1 instance for a given 'message' or 'enum'
class NumberManager(private val startAt: Int = 1) {
    private val numberByName = mutableMapOf<String, Int>()

    fun resolve(name: String, number: Int?): Int {
        numberByName[name]?.let { return it }
        val resolvedNumber = number ?: ((numberByName.values.maxOrNull() ?: (startAt - 1)) + 1)
        numberByName[name] = resolvedNumber
        return resolvedNumber
    }
}