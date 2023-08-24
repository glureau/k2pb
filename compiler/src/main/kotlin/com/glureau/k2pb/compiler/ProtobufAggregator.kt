package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.mapping.InlinedTypeRecorder
import com.glureau.k2pb.compiler.struct.*

class ProtobufAggregator() {
    private val messages = mutableListOf<MessageNode>()
    private val enums = mutableListOf<EnumNode>()
    private val qualifiedNameSet = mutableSetOf<String>()

    fun recordMessageNode(it: MessageNode) {
        messages += it
        require(qualifiedNameSet.contains(it.qualifiedName).not()) { "Duplicated qualified name: ${it.qualifiedName}" }
        qualifiedNameSet += it.qualifiedName
        Logger.warn("recordMessageNode ${it.qualifiedName} => ${it.name}")
        TypeResolver.qualifiedNameToProtobufName[it.qualifiedName] = it.name
    }

    fun recordEnumNode(it: EnumNode) {
        enums += it
        require(qualifiedNameSet.contains(it.qualifiedName).not()) { "Duplicated qualified name: ${it.qualifiedName}" }
        qualifiedNameSet += it.qualifiedName
        Logger.warn("recordEnumNode ${it.qualifiedName} => ${it.name}")
        TypeResolver.qualifiedNameToProtobufName[it.qualifiedName] = it.name
    }

    fun unknownReferences(): Set<String> {
        val fieldTypeList: List<FieldType> = messages.flatMap { it.fields }.flatMap { it.allFieldTypes() }
        val stdRefs = fieldTypeList.filterIsInstance<ReferenceType>().map { it.name }
        // TODO: recursivity + TU for List@Map
        val listRefs = fieldTypeList.filterIsInstance<ListType>()
            .mapNotNull { if (it.repeatedType is ReferenceType) it.repeatedType.name else null }
        val mapRefs = fieldTypeList.filterIsInstance<MapType>()
            .flatMap {
                val list = mutableListOf<String>()
                if (it.keyType is ReferenceType) list.add(it.keyType.name)
                if (it.valueType is ReferenceType) list.add(it.valueType.name)
                list
            }
        val references: Set<String> = (stdRefs + listRefs + mapRefs).toSet()
        return references - (qualifiedNameSet + InlinedTypeRecorder.getAllInlinedTypes().keys)
    }

    fun buildFiles(moduleName: String): Sequence<ProtobufFile> {
        val importResolver = ImportResolver { protobufName ->
            "${protobufName.substringBefore(".")}.proto"
        }

        // TODO: warning, import computation is slightly correlated to this split logic (1 class by file)
        val updatedMessages = updateMessageForNesting(messages, enums)
        return sequence<ProtobufFile> {
            updatedMessages.forEach { messageNode ->
                if (messageNode.originalFile == null) return@forEach // Skip messages without original file
                // TODO: this could be an option instead of default behavior
                if (!messageNode.originalFile.filePath.contains(moduleName)) return@forEach // Skip messages from other modules

                yield(ProtobufFile(
                    path = "k2pb/${messageNode.name}",
                    packageName = null,
                    syntax = ProtoSyntax.v3,
                    messages = listOf(messageNode),
                    enums = emptyList(),
                    imports = computeImports(
                        messageNodes = listOf(messageNode),
                        enumNodes = listOf(),
                        locallyDeclaredReferences = messageNode.declaredReferences,
                        importResolver = importResolver
                    )
                ))
            }
            enums.mapNotNull { enumNode ->
                if (enumNode.name.contains(".")) return@mapNotNull null // Skip nested enums
                yield(ProtobufFile(
                    path = "k2pb/${enumNode.name}",
                    packageName = null,
                    syntax = ProtoSyntax.v3,
                    messages = emptyList(),
                    enums = listOf(enumNode),
                    imports = computeImports(
                        messageNodes = listOf(),
                        enumNodes = listOf(enumNode),
                        locallyDeclaredReferences = enumNode.declaredReferences,
                        importResolver = importResolver,
                    )
                ))
            }
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
