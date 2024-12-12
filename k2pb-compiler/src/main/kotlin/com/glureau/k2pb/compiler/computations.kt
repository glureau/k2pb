package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.mapping.customConverterType
import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.FieldInterface
import com.glureau.k2pb.compiler.struct.FieldType
import com.glureau.k2pb.compiler.struct.ListType
import com.glureau.k2pb.compiler.struct.MapType
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.Node
import com.glureau.k2pb.compiler.struct.OneOfField
import com.glureau.k2pb.compiler.struct.ProtobufFile
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.TypedField
import com.google.devtools.ksp.symbol.KSFile


val ProtobufFile.dependencies: List<KSFile>
    get() = (messages.flatMap { it.dependencies } + enums.mapNotNull { it.originalFile }).distinct()

internal val Node.declaredReferences: List<String>
    get() = when (this) {
        is MessageNode -> this.nestedNodes.flatMap { it.declaredReferences } + this.qualifiedName
        is EnumNode -> listOf(this.qualifiedName)
    }

fun interface ImportResolver {
    // Return file to import
    fun resolve(protobufName: String): String
}

private fun collectNodes(
    messageNodes: List<MessageNode>,
    enumNodes: List<EnumNode>,
): List<Node> {
    return messageNodes.flatMap { it.nestedNodes } + enumNodes
}

private fun MessageNode.allNodes(): List<Node> {
    return this.nestedNodes.flatMap {
        when (it) {
            is MessageNode -> it.allNodes()
            is EnumNode -> listOf(it)
        }
    } + this
}

fun computeImports(
    messageNodes: List<MessageNode>,
    enumNodes: List<EnumNode>,
    importResolver: ImportResolver,
    locallyDeclaredReferences: List<String>,
): List<String> {
    val allNodes = messageNodes.flatMap { it.allNodes() } + enumNodes
    val allTypeReferences = allNodes.flatMap { node ->
        when (node) {
            is MessageNode -> node.fields.flatMap { it.resolvedExternalTypes() }
            is EnumNode -> listOf(node.name)
        }
    }.distinct()

    return (allTypeReferences - locallyDeclaredReferences.toSet())
        .mapNotNull { TypeResolver.qualifiedNameToProtobufName[it] }
        .map { importResolver.resolve(it) }
        .distinct()
}

fun FieldInterface.resolvedExternalTypes(): List<String> {
    return when (this) {
        is TypedField -> {
            if (this.annotatedConverter.customConverterType() != null) {
                emptyList()
            } else {
                this.type.resolvedExternalTypes()
            }
        }

        is OneOfField -> this.fields.flatMap { it.resolvedExternalTypes() }
    }
}

fun FieldType.resolvedExternalTypes(): List<String> {
    return when (this) {
        is ListType -> {
            repeatedType.resolvedExternalTypes()
        }

        is MapType -> {
            keyType.resolvedExternalTypes() + valueType.resolvedExternalTypes()
        }

        is ScalarFieldType -> emptyList()
        is ReferenceType -> {
            if (this.inlineAnnotatedSerializer.customConverterType() != null) {
                emptyList()
            } else {
                this.inlineOf?.resolvedExternalTypes() ?: listOf(this.name)
            }
        }
    }
}