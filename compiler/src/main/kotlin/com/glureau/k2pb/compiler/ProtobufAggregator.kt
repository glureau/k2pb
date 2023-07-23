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
        } + enums.map { enumNode ->
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
    val parentNameChildMessages = mutableMapOf<String, MutableList<MessageNode>>()
    val parentNameChildEnums = mutableMapOf<String, MutableList<EnumNode>>()
    messages.forEach {
        val parentName = it.name.substringBeforeLast(".")
        Logger.warn("parentName: $parentName != ${it.name} => ${parentName != it.name}")
        if (parentName != it.name) {
            Logger.warn("FOUND 1 relationship!")
            val list = parentNameChildMessages.getOrDefault(parentName, mutableListOf())
            list.add(it)
            parentNameChildMessages[parentName] = list
            Logger.warn("FOUND parentNameChildMessages=$parentNameChildMessages")

        }
    }
    enums.forEach {
        val parentName = it.name.substringBeforeLast(".")
        if (parentName != it.name) {
            val list = parentNameChildEnums.getOrDefault(parentName, mutableListOf())
            list.add(it)
            parentNameChildEnums[parentName] = list
        }
    }

    return messages.mapNotNull { original ->
        if (original.name.contains(".").not()) {
            return@mapNotNull original.copy(
                nestedNodes = nestedNodes(
                    parentNameChildMessages,
                    parentNameChildEnums,
                    original
                )
            )
        } else null
    }
}

fun nestedNodes(
    parentNameChildMessages: MutableMap<String, MutableList<MessageNode>>,
    parentNameChildEnums: MutableMap<String, MutableList<EnumNode>>,
    original: MessageNode,
): List<Node> {
    Logger.warn("-----------------")
    val directNestedRegex = Regex(original.name + ".[a-zA-Z]+")
    Logger.warn("nestedNodes(${original.name}) '$directNestedRegex'")
    val directMessages: List<MessageNode> = parentNameChildMessages[original.name]
        ?.map { it.copy(nestedNodes = nestedNodes(parentNameChildMessages, parentNameChildEnums, it)) }
        ?: emptyList()
    Logger.warn("filterKeys = ${parentNameChildMessages.filterKeys { it.matches(directNestedRegex) }}")

    // TODO: Use Regex and avoid tmp maps?
    val directEnums = parentNameChildEnums.filterKeys { it.matches(directNestedRegex) }.values.flatten()
    Logger.warn("nestedNodes(${original.name}) '$directNestedRegex' => $directMessages + $directEnums")
    return directMessages + directEnums
}
