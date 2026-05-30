package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.protofile.resolvedExternalTypes
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.Node
import com.glureau.k2pb.compiler.struct.nullabilityQualifiedName

class ProtobufAggregator {
    internal val nodes = mutableListOf<Node>()

    private val qualifiedNameSet = mutableSetOf<String>()

    fun recordNode(node: Node) {
        if (!qualifiedNameSet.add(node.qualifiedName)) {
            Logger.info("Skipping already-recorded node: ${node.qualifiedName}")
            return
        }
        nodes += node
        TypeResolver.recordNode(node)
    }

    fun markResolved(qualifiedName: String) {
        qualifiedNameSet += qualifiedName
    }

    fun unknownReferences(): Set<String> {
        val references: Set<String> = nodes
            .filterIsInstance<MessageNode>()
            .flatMap { it.fields }
            .flatMap { it.resolvedExternalTypes() }
            .toSet()
        return references - qualifiedNameSet
    }
}

