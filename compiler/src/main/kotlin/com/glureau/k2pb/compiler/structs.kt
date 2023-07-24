package com.glureau.k2pb.compiler

import com.google.devtools.ksp.symbol.KSFile


enum class ProtoSyntax { v3 } // We don't deal with v2 at all for now...


interface FieldType

data class ReferenceType(val name: String) : FieldType {
    override fun toString() = name
}

data class ListType(val repeatedType: FieldType) : FieldType {
    override fun toString(): String = "repeated $repeatedType"
}

data class MapType(val keyType: FieldType, val valueType: FieldType) : FieldType {
    override fun toString(): String = "map<$keyType, $valueType>"
}

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
    val comment: String?,
    val type: FieldType,
    val name: String,
    val number: Int,
) {
    override fun toString(): String {
        var result = ""
        result += comment.toProtobufComment()
        result += when (type) {
            is ScalarType -> type.name
            // Protobuf name COULD be simplified in function of the location, but a bit more complex to implement and
            // both solutions are valid for protobuf.
            is ReferenceType -> TypeResolver.qualifiedNameToProtobufName[type.name] ?: type.name
            is ListType -> type.toString()
            is MapType -> type.toString()
            else -> error("unknown type $type")
        }
        result += " $name = $number;"
        return result
    }
}

sealed class Node {
    abstract val qualifiedName: String
    abstract val name: String
    abstract val originalFile: KSFile?
}

data class MessageNode(
    override val qualifiedName: String,
    override val name: String,
    val comment: String?,
    val fields: List<Field>,
    override val originalFile: KSFile?,
) : Node() {
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
    override fun toString(): String {
        var result = comment.toProtobufComment()
        result += "message ${name.substringAfterLast(".")} {\n"
        if (fields.isNotEmpty()) result += fields.joinToString("\n").prependIndent("  ") + "\n"
        if (nestedNodes.isNotEmpty()) result += nestedNodes.joinToString("\n").prependIndent("  ") + "\n"
        return "$result}"
    }
}

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

data class EnumEntry(val name: String, val comment: String?, val number: Int) {
    override fun toString(): String {
        var result = ""
        result += comment.toProtobufComment()
        return "$result$name = $number;"
    }
}

fun String?.toProtobufComment(): String = if (!isNullOrBlank()) "/*$this*/\n" else ""

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
