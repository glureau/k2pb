package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.appendComment
import com.google.devtools.ksp.symbol.KSFile

data class MessageNode(
    override val qualifiedName: String,
    override val name: String,
    val comment: String?,
    val fields: List<FieldInterface>,
    override val originalFile: KSFile?,
) : Node() {
    val numberManager = NumberManager()
    val dependencies: List<KSFile>
        get() {
            val result = mutableListOf<KSFile>()
            originalFile?.let { result.add(it) }
            nestedNodes.forEach { node ->
                node.originalFile?.let { result.add(it) }
            }
            return result
        }
    val nestedNodes: MutableList<Node> = mutableListOf()

}

fun StringBuilder.appendMessageNode(indentLevel: Int, messageNode: MessageNode) {
    appendComment(indentLevel, messageNode.comment)
    appendLineWithIndent(indentLevel, "message ${messageNode.name.substringAfterLast(".")} {")
    messageNode.fields.forEach {
        appendField(indentLevel + 1, it, messageNode.numberManager)
    }
    messageNode.nestedNodes.forEach {
        when (it) {
            is MessageNode -> appendMessageNode(indentLevel + 1, it)
            is EnumNode -> appendEnumNode(indentLevel + 1, it)
        }
    }
    appendLineWithIndent(indentLevel, "}")
}