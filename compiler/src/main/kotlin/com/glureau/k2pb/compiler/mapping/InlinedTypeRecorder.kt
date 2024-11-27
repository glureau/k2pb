package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.struct.FieldType

object InlinedTypeRecorder {
    data class InlineNode(
        val qualifiedName: String,
        val inlinedFieldType: FieldType,
        val inlineName: String,
    )

    private val inlinedTypes = mutableMapOf<String, InlineNode>()

    fun recordInlinedType(inlineNode: InlineNode) {
        inlinedTypes[inlineNode.qualifiedName] = inlineNode
    }

    fun getInlinedType(qualifiedName: String): InlineNode? = inlinedTypes[qualifiedName]

    fun getAllInlinedNodes(): Map<String, InlineNode> = inlinedTypes.toMap()
}