package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.EnumNode

fun StringBuilder.appendEnumNode(indentLevel: Int, enumNode: EnumNode) {
    appendComment(indentLevel, enumNode.comment)
    appendLineWithIndent(indentLevel, "enum ${enumNode.name.substringAfterLast(".")} {")
    enumNode.entries.forEach {
        appendEnumEntry(indentLevel + 1, it)
    }
    appendLineWithIndent(indentLevel, "}")
}