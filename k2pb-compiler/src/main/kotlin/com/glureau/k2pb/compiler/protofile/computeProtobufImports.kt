package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.ImportResolver
import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.TypeResolver
import com.glureau.k2pb.compiler.declaredReferences
import com.glureau.k2pb.compiler.mapping.customConverterType
import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.FieldInterface
import com.glureau.k2pb.compiler.struct.FieldType
import com.glureau.k2pb.compiler.struct.ListType
import com.glureau.k2pb.compiler.struct.MapType
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.Node
import com.glureau.k2pb.compiler.struct.ObjectNode
import com.glureau.k2pb.compiler.struct.OneOfField
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.nullabilityQualifiedName


fun computeProtobufImports(
    nodes: List<Node>,
    importResolver: ImportResolver,
    locallyDeclaredReferences: List<String>,
): List<String> {
    val allNodes = nodes.flatMap { it.allNodes() }
    val allTypeReferences = allNodes.flatMap { node ->
        when (node) {
            is MessageNode -> node.fields.flatMap { it.resolvedExternalTypes() }
            is EnumNode -> emptyList()
            is ObjectNode -> emptyList()
        }
    }.distinct()
    val selfRef = nodes.map { it.qualifiedName }

    return (allTypeReferences - locallyDeclaredReferences.toSet())
        // Filtering out self references, hypothesis: qfn <=> nesting in same file...
        .filter { selfRef.none { sr -> it.startsWith("$sr.") } }
        .mapNotNull { TypeResolver.resolveName(it) }
        .map { importResolver.resolve(it) }
        .distinct()
}

private fun Node.allNodes(): List<Node> {
    return this.nestedNodes.flatMap {
        it.allNodes()
    } + this
}

fun FieldInterface.resolvedExternalTypes(): List<String> {
    return when (this) {
        is TypedField -> {
            buildList {
                if (nullabilitySubField != null) {
                    add(nullabilityQualifiedName)
                }
                if (annotatedConverter.customConverterType() != null) {
                    /* ... */
                } else {
                    addAll(type.resolvedExternalTypes())
                }
            }
        }

        is OneOfField -> this.activeFields.flatMap { it.resolvedExternalTypes() }
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
            if (this.inlineAnnotatedCodec.customConverterType() != null) {
                emptyList()
            } else {
                this.inlineOf?.resolvedExternalTypes() ?: listOf(this.className.canonicalName)
            }
        }
    }
}

fun computeDeprecatedProtobufImports(nodes: List<Node>, importResolver: ImportResolver): List<String> {
    val selfRef = nodes.map { it.protoName }
    Logger.warn("computeDeprecatedProtobufImports : $selfRef")

    return nodes.flatMap { it.allNodes() }
        .flatMap {
            it.declaredReferences
            when (it) {
                is MessageNode -> it.fields.flatMap {
                    when (it) {
                        is OneOfField -> it.deprecatedFields
                        else -> emptyList()
                    }
                }

                is EnumNode -> emptyList()
                is ObjectNode -> emptyList()
            }
        }
        .filter { it.publishedInProto }
        .map { importResolver.resolve(it.protoName) } -
            selfRef.map { importResolver.resolve(it) }

}