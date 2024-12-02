package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.appendComment
import com.google.devtools.ksp.symbol.KSFile

data class EnumNode(
    override val qualifiedName: String,
    override val name: String,
    val comment: String?,
    val entries: List<EnumEntry>,
    override val originalFile: KSFile?,
) : Node() {
    override val generatesNow: Boolean get() = true
}

fun StringBuilder.appendEnumNode(indentLevel: Int, enumNode: EnumNode) {
    appendComment(indentLevel, enumNode.comment)
    appendLineWithIndent(indentLevel, "enum ${enumNode.name.substringAfterLast(".")} {")
    enumNode.entries.forEach {
        appendEnumEntry(indentLevel + 1, it)
    }
    appendLineWithIndent(indentLevel, "}")
}