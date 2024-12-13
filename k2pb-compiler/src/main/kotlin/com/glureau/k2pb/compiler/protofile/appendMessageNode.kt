package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.MessageNode

fun StringBuilder.appendMessageNode(indentLevel: Int, messageNode: MessageNode) {
    appendComment(indentLevel, messageNode.comment)
    appendLineWithIndent(indentLevel, "message ${messageNode.name.substringAfterLast(".")} {")
    messageNode.fields.forEach {
        appendField(indentLevel + 1, it, messageNode.numberManager)
    }
    messageNode.nestedNodes
        //.filter { it.generatesNow }
        .forEach {
            when (it) {
                is MessageNode -> appendMessageNode(indentLevel + 1, it)
                is EnumNode -> appendEnumNode(indentLevel + 1, it)
            }
        }
    appendLineWithIndent(indentLevel, "}")
}
