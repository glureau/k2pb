package com.glureau.k2pb.compiler.struct

// 1 instance for a given 'message'
class NumberManager {
    private val numberByName = mutableMapOf<String, Int>()

    fun resolve(name: String, number: Int?): Int {
        numberByName[name]?.let { return it }
        val resolvedNumber = number ?: ((numberByName.values.maxOrNull() ?: 0) + 1)
        numberByName[name] = resolvedNumber
        return resolvedNumber
    }
}