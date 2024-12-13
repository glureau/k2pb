package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.protofile.ProtobufFile
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.Node
import com.glureau.k2pb.compiler.struct.ProtoSyntax

class ProtobufFileProducer(private val aggregator: ProtobufAggregator) {
    fun buildFiles(moduleName: String): Sequence<ProtobufFile> {
        val importResolver = ImportResolver { protobufName ->
            "${protobufName.substringBefore(".")}.proto"
        }

        // TODO: warning, import computation is slightly correlated to this split logic (1 class by file)
        val updatedMessages = updateMessageForNesting(aggregator.nodes)
        return sequence {
            updatedMessages
                .filterNot { it is MessageNode && it.isInlineClass } // Skip inline classes, to be challenged...
                .forEach { node ->
                    if (node.originalFile == null) {
                        Logger.info(
                            "No original file for ${node.name} " +
                                    "(possibly coming from another module/library), skipping generation..."
                        )
                        return@forEach
                    }
                    // TODO: this could be an option instead of default behavior,
                    //  also it may be useless given originalFile should be null for classes coming from other libs...
                    if (!node.originalFile!!.filePath.contains(moduleName)) {
                        Logger.info(
                            "Skipping message from other module (current module = $moduleName): " +
                                    "${node.name} // ${node.originalFile!!.filePath}"
                        )
                        return@forEach
                    }

                    val imports = computeImports(
                        nodes = listOf(node),
                        locallyDeclaredReferences = node.declaredReferences,
                        importResolver = importResolver
                    )

                    yield(
                        ProtobufFile(
                            path = "k2pb/${node.name}",
                            packageName = null,
                            syntax = ProtoSyntax.v3,
                            nodes = listOf(node),
                            imports = imports
                        )
                    )
                }
        }
    }
}

private fun updateMessageForNesting(nodes: List<Node>): List<Node> {
    val parentNameChildNodes = nodes.associateBy { it.name }
    val updatedNodes = mutableListOf<Node>()
    nodes.sortedBy { it.name }
        .forEach {
            val parentName = it.name.substringBeforeLast(".")
            if (parentName == it.name) {
                // No parent, this is a root message
                updatedNodes += it
            } else {
                parentNameChildNodes[parentName]?.nestedNodes?.add(it)
                    ?: error("Parent message not found for ${it.name}")
            }
        }
    return updatedNodes
}
