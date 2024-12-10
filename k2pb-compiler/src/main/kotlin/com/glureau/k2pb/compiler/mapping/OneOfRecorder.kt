package com.glureau.k2pb.compiler.mapping

object OneOfRecorder {
    private val oneOfs = mutableMapOf<String, List<String>>()
    fun recordSealedClass(sealedParent: String, sealedSubClasses: List<String>) {
        oneOfs[sealedParent] = sealedSubClasses
    }

    fun getSealedSubClasses(name: String): List<String>? = oneOfs[name]

}
