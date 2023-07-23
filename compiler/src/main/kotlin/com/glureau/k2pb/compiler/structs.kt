package com.glureau.k2pb.compiler


enum class ProtoSyntax { v3 } // We don't deal with v2 at all for now...


interface FieldType {
    companion object {
        fun from(str: String): FieldType = ScalarType.values().firstOrNull { it.name == str }
            ?: ReferenceType(str)
    }
}

data class ReferenceType(val name: String) : FieldType
enum class ScalarType : FieldType { // https://protobuf.dev/programming-guides/proto3/#scalar
    double,
    float,
    int32,
    int64,
    uint32,
    uint64,
    sint32,
    sint64,
    fixed32,
    fixed64,
    sfixed32,
    sfixed64,
    bool,
    string,
    bytes,
}

data class Field(
    val comment: List<String>,
    val repeated: Boolean,
    val type: FieldType,
    val name: String,
    val number: Int,
) {
    override fun toString(): String {
        var result = ""
        result += comment.toProtobufComment()
        if (repeated) result += "repeated "
        result += when (type) {
            is ScalarType -> type.name
            is ReferenceType -> type.name
            else -> error("unknown type $type")
        }
        result += " $name = $number;"
        return result
    }
}

sealed class Node

data class MessageNode(
    val name: String,
    val comment: List<String>,
    val nestedNodes: List<Node>,
    val fields: List<Field>,
) : Node() {
    override fun toString(): String {
        var result = ""
        result += comment.toProtobufComment()
        result += "message $name {\n"
        if (fields.isNotEmpty()) result += fields.joinToString("\n").prependIndent("  ") + "\n"
        if (nestedNodes.isNotEmpty()) result += nestedNodes.joinToString("\n").prependIndent("  ") + "\n"
        return "$result}\n"
    }
}

data class EnumNode(val name: String, val comment: List<String>, val entries: List<EnumEntry>) : Node() {
    override fun toString(): String {
        var result = ""
        result += comment.toProtobufComment()
        result += "enum $name {\n"
        if (entries.isNotEmpty()) result += entries.joinToString("\n").prependIndent("  ") + "\n"
        return "$result}\n"
    }
}

data class EnumEntry(val name: String, val comment: List<String>, val number: Int) {
    override fun toString(): String {
        var result = ""
        result += comment.toProtobufComment()
        return "$result$name = $number;"
    }
}

fun List<String>.toProtobufComment(): String =
    if (isNotEmpty()) "/*" + joinToString("\n") + "*/\n"
    else ""

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
        } + "\";\n"
        if (imports.isNotEmpty()) result += imports.joinToString("\n") { "import \"$it\";" } + "\n\n"
        if (packageName != null) result += "package $packageName;\n\n"
        if (messages.isNotEmpty()) result += messages.joinToString("\n\n") + "\n\n"
        if (enums.isNotEmpty()) result += enums.joinToString("\n\n") + "\n\n"
        return result
    }
}

