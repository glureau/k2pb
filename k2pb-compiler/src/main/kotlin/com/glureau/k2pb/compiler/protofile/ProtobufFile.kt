package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.Node
import com.glureau.k2pb.compiler.struct.ProtoSyntax

data class ProtobufFile(
    val path: String,
    val packageName: String? = null,
    val syntax: ProtoSyntax = ProtoSyntax.v3,
    val nodes: List<Node>,
    val imports: List<String>,
) {

    fun toProtoString(): String = buildString {
        append("syntax = \"")
        when (syntax) {
            ProtoSyntax.v3 -> append("proto3")
        }
        appendLine("\";")
        appendLine()

        imports.forEach { appendLine("import \"$it\";") }
        if (imports.isNotEmpty()) appendLine()

        if (packageName != null) appendLine("package $packageName;\n")

        nodes.forEach { appendNode(0, it) }
    }
}