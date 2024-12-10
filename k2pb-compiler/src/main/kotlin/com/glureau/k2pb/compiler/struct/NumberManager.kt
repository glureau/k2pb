package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.Logger

// 1 instance for a given 'message' or 'enum'
class NumberManager(private val startAt: Int = 1) {
    private val numberByName = mutableMapOf<String, Int>()
    val nextNumberPreview get() = (numberByName.values.maxOrNull() ?: (startAt - 1)) + 1

    fun resolve(name: String, annotatedNumber: Int?): Int {
        numberByName[name]?.let {
            if (annotatedNumber != null && it != annotatedNumber) {
                Logger.error("Field $name is already assigned with a different number. Original=$it, Annotated=$annotatedNumber")
            }
            return it
        }
        val resolvedNumber = annotatedNumber ?: nextNumberPreview
        numberByName[name] = resolvedNumber
        return resolvedNumber
    }
}