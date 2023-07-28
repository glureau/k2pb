package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.toProtobufComment
import com.google.devtools.ksp.symbol.KSFile

data class EnumNode(
    override val qualifiedName: String,
    override val name: String,
    val comment: String?,
    val entries: List<EnumEntry>,
    override val originalFile: KSFile?,
) : Node() {
    override fun toString(): String {
        var result = ""
        result += comment.toProtobufComment()
        result += "enum ${name.substringAfterLast(".")} {\n"
        if (entries.isNotEmpty()) result += entries.joinToString("\n").prependIndent("  ") + "\n"
        return "$result}"
    }
}
