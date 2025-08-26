package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.Logger

// 1 instance for a given 'message' or 'enum'
class NumberManager(private val startAt: Int = 1, deprecatedProtoNumbers: List<Int>) {
    private val deprecatedNumbers = deprecatedProtoNumbers.toSet()
    private val numberByName = mutableMapOf<String, Int>()

    fun resolve(name: String, annotatedNumber: Int?): Int {
        numberByName[name]?.let {
            if (annotatedNumber != null && it != annotatedNumber) {
                Logger.error("Field $name is already assigned with a different number. Original=$it, Annotated=$annotatedNumber")
            }
            return it
        }
        val resolvedNumber = annotatedNumber ?: run {
            val used = deprecatedNumbers + numberByName.values
            var current = startAt
            while (used.contains(current)) {
                current++
            }
            current
        }
        numberByName[name] = resolvedNumber
        return resolvedNumber
    }
}