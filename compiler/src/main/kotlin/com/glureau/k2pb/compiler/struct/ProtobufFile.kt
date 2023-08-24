package com.glureau.k2pb.compiler.struct

data class ProtobufFile(
    val path: String,
    val packageName: String? = null,
    val syntax: ProtoSyntax = ProtoSyntax.v3,
    val messages: List<MessageNode>,
    val enums: List<EnumNode>,
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

        messages.forEach { appendMessageNode(0, it) }

        enums.forEach { appendEnumNode(0, it) }
    }
}
