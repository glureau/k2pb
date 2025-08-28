package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.MessageNode

fun StringBuilder.appendMessageNode(indentLevel: Int, messageNode: MessageNode) {
    appendComment(indentLevel, messageNode.comment)
    appendLineWithIndent(indentLevel, "message ${messageNode.protoName.substringAfterLast(".")} {")
    appendReservedFields(indentLevel + 1, messageNode.deprecatedFields)
    appendFields(
        indentLevel = indentLevel + 1,
        activeFields = messageNode.fields,
        deprecatedFields = messageNode.deprecatedFields,
        debugName = messageNode.protoName,
    )

    messageNode.nestedNodes
        .filter { it.generatesNow }
        .forEach {
            appendNode(indentLevel + 1, it)
        }
    appendLineWithIndent(indentLevel, "}")
}
