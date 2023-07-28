package com.glureau.k2pb.compiler.struct
data class ProtobufFile(
    val path: String,
    val packageName: String? = null,
    val syntax: ProtoSyntax = ProtoSyntax.v3,
    val messages: List<MessageNode>,
    val enums: List<EnumNode>,
    val imports: List<String>,
) {

    override fun toString(): String {
        var result = "syntax = \"" + when (syntax) {
            ProtoSyntax.v3 -> "proto3"
        } + "\";\n\n"
        if (imports.isNotEmpty()) result += imports.joinToString("\n") { "import \"$it\";" } + "\n\n"
        if (packageName != null) result += "package $packageName;\n\n"
        if (messages.isNotEmpty()) result += messages.joinToString("\n\n") + "\n"
        if (enums.isNotEmpty()) result += enums.joinToString("\n\n") + "\n"
        return result
    }
}
