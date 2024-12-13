package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.ProtoSyntax
import com.glureau.k2pb.compiler.protofile.ProtobufFile

class ProtobufFileProducer(private val aggregator: ProtobufAggregator) {
    fun buildFiles(moduleName: String): Sequence<ProtobufFile> {
        val importResolver = ImportResolver { protobufName ->
            "${protobufName.substringBefore(".")}.proto"
        }

        // TODO: warning, import computation is slightly correlated to this split logic (1 class by file)
        val updatedMessages = updateMessageForNesting(aggregator.messages, aggregator.enums)
        return sequence {
            updatedMessages.filter { !it.isInlineClass }
                .forEach { messageNode ->
                    if (messageNode.originalFile == null) {
                        Logger.info(
                            "No original file for ${messageNode.name} " +
                                    "(possibly coming from another module/library), skipping generation..."
                        )
                        return@forEach
                    }
                    // TODO: this could be an option instead of default behavior,
                    //  also it may be useless given originalFile should be null for classes coming from other libs...
                    if (!messageNode.originalFile.filePath.contains(moduleName)) {
                        Logger.info(
                            "Skipping message from other module (current module = $moduleName): " +
                                    "${messageNode.name} // ${messageNode.originalFile.filePath}"
                        )
                        return@forEach
                    }

                    yield(
                        ProtobufFile(
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
                        )
                    )
                }
            aggregator.enums.forEach { enumNode ->
                if (enumNode.originalFile == null) {
                    Logger.info(
                        "No original file for ${enumNode.name} " +
                                "(possibly coming from another module/library), skipping generation..."
                    )
                    return@forEach
                }
                // TODO: this could be an option instead of default behavior,
                //  also it may be useless given originalFile should be null for classes coming from other libs...
                if (!enumNode.originalFile.filePath.contains(moduleName)) {
                    Logger.info(
                        "Skipping message from other module (current module = $moduleName): " +
                                "${enumNode.name} // ${enumNode.originalFile.filePath}"
                    )
                    return@forEach
                }

                if (enumNode.name.contains(".")) return@forEach // Skip nested enums
                yield(
                    ProtobufFile(
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
                    )
                )
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
