package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.Node
import com.glureau.k2pb.compiler.struct.ObjectNode

fun StringBuilder.appendNode(indentLevel: Int, node: Node) {
    when (node) {
        is MessageNode -> appendMessageNode(indentLevel, node)
        is EnumNode -> appendEnumNode(indentLevel, node)
        is ObjectNode -> appendObjectNode(indentLevel, node)
    }
}