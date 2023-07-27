package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.FieldType

object InlinedTypeRecorder {
    private val inlinedTypes = mutableMapOf<String, FieldType>()

    fun recordInlinedType(qualifiedName: String, inlinedFieldType: FieldType) {
        inlinedTypes[qualifiedName] = inlinedFieldType
    }

    fun getInlinedType(qualifiedName: String): FieldType? = inlinedTypes[qualifiedName]

    fun getAllInlinedTypes(): Map<String, FieldType> = inlinedTypes.toMap()
}