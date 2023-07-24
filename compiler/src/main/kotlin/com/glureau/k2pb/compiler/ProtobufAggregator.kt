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

        val updatedMessages = updateMessageForNesting(messages, enums)

        // TODO: Detect imports
        return updatedMessages.map { messageNode ->
            ProtobufFile(
                path = "TODO/${messageNode.name}.proto",
                packageName = null,
                syntax = ProtoSyntax.v3,
                messages = listOf(messageNode),
                enums = emptyList(),
                imports = listOf()
            )
        } + enums.mapNotNull { enumNode ->
            if (enumNode.name.contains(".")) return@mapNotNull null // Skip nested enums
            ProtobufFile(
                path = "TODO/$enumNode.proto",
                packageName = null,
                syntax = ProtoSyntax.v3,
                messages = emptyList(),
                enums = listOf(enumNode),
                imports = listOf()
            )
        }
    }
}

private fun updateMessageForNesting(messages: List<MessageNode>, enums: MutableList<EnumNode>): List<MessageNode> {
    val parentNameChildMessages = messages.associateBy { it.name }
    val updatedMessages = mutableListOf<MessageNode>()
    messages.sortedBy { it.name }
        .forEach {
            val parentName = it.name.substringBeforeLast(".")
            if (parentName == it.name) {
                // No parent, this is a root message
                updatedMessages += it
            } else {
                parentNameChildMessages[parentName]?.nestedNodes?.add(it)
                    ?: error("Parent message not found for ${it.name}")
            }
        }
    enums.forEach {
        val parentName = it.name.substringBeforeLast(".")
        if (parentName != it.name) {
            parentNameChildMessages[parentName]?.nestedNodes?.add(it)
                ?: error("Parent message not found for enum ${it.name}")
        }
    }

    return updatedMessages
}
