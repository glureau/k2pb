package com.glureau.k2pb.compiler

class ProtobufAggregator {
    private val messages = mutableListOf<MessageNode>()
    private val enums = mutableListOf<EnumNode>()
    private val qualifiedNameSet = mutableSetOf<String>()

    fun recordMessageNode(it: MessageNode) {
        messages += it
        require(qualifiedNameSet.contains(it.qualifiedName).not()) { "Duplicated qualified name: ${it.qualifiedName}" }
        qualifiedNameSet += it.qualifiedName
    }

    fun recordEnumNode(it: EnumNode) {
        enums += it
        require(qualifiedNameSet.contains(it.qualifiedName).not()) { "Duplicated qualified name: ${it.qualifiedName}" }
        qualifiedNameSet += it.qualifiedName
    }

    fun unknownReferences(): Set<String> {
        val references = messages.flatMap { it.fields }
            .map { it.type }
            .filterIsInstance<ReferenceType>()
            .map { it.name }
            .toSet()
        return references - qualifiedNameSet
    }

    fun buildFiles(): List<ProtobufFile> {
        require(unknownReferences().isEmpty()) { "Unknown references: ${unknownReferences().joinToString()}" }

        // TODO: Detect nesting
        // TODO: Detect imports
        return listOf(
            ProtobufFile(
                path = "TODO",
                packageName = null,
                syntax = ProtoSyntax.v3,
                messages = messages,
                enums = enums,
                imports = listOf()
            )
        )
    }
}
