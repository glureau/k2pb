package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.MessageNode

class ProtobufAggregator {
    internal val messages = mutableListOf<MessageNode>()
    internal val enums = mutableListOf<EnumNode>()

    private val qualifiedNameSet = mutableSetOf<String>()

    fun recordMessageNode(it: MessageNode) {
        messages += it
        require(qualifiedNameSet.contains(it.qualifiedName).not()) { "Duplicated qualified name: ${it.qualifiedName}" }
        qualifiedNameSet += it.qualifiedName
        Logger.warn("recordMessageNode ${it.qualifiedName} => ${it.name}")
        //if (it.isInlineClass) return // Ignore inlined class, for protobuf imports
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
        val references: Set<String> = messages
            .flatMap { it.fields }
            .flatMap { it.resolvedExternalTypes() }
            .toSet()
        return references - qualifiedNameSet
    }
}

