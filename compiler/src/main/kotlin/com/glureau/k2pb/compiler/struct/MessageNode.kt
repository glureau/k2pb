package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.toProtobufComment
import com.google.devtools.ksp.symbol.KSFile

data class MessageNode(
    override val qualifiedName: String,
    override val name: String,
    val comment: String?,
    val fields: List<FieldInterface>,
    override val originalFile: KSFile?,
) : Node() {
    val numberManager = NumberManager()
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
        //var lastUsedFieldNumber = 0
        if (fields.isNotEmpty()) {
            result += fields.joinToString("\n", transform = { it.toString(numberManager) })
                .prependIndent("  ") + "\n"
            /*
            TODO: Keeping this block, as we'll need it to handle properly numbers in oneOfs.
            fields.forEach { field ->
                when (field) {
                    is OneOfField -> {
                        result += "  oneof ${field.name} {\n"
                        fields.forEach { subclass ->
                            result += "    ${TypeResolver.qualifiedNameToProtobufName[subclass.name] ?: subclass} $subclass = ${lastUsedFieldNumber++};\n"
                        }
                    }

                    is TypedField -> result += "  $field\n"
                }
            }*/
        }
        if (nestedNodes.isNotEmpty()) result += nestedNodes.joinToString("\n").prependIndent("  ") + "\n"
        return "$result}"
    }
}