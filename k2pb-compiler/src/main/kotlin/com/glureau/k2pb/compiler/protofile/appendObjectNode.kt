package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.ObjectNode

fun StringBuilder.appendObjectNode(indentLevel: Int, objectNode: ObjectNode) {
    appendComment(indentLevel, objectNode.comment)
    appendLineWithIndent(indentLevel, "message ${objectNode.name.substringAfterLast(".")} { }")
}