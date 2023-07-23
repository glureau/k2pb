package com.glureau.k2pb.compiler

class ProtobufAggregator {
    private val messages = mutableListOf<MessageNode>()
    private val enums = mutableListOf<EnumNode>()
    fun recordMessageNode(it: MessageNode) {
        messages += it
    }

    fun recordEnumNode(it: EnumNode) {
        enums += it
    }

    fun buildFiles(): List<ProtobufFile> {
        // TODO: Detect all classes that have no node, potentially getting enums without @Serializable (as it's not required)
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
