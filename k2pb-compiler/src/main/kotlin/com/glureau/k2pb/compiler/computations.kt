package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.mapping.customConverterType
import com.glureau.k2pb.compiler.protofile.ProtobufFile
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
import com.google.devtools.ksp.symbol.KSFile


val ProtobufFile.dependencies: List<KSFile>
    get() = nodes.mapNotNull { it.originalFile }.distinct()

internal val Node.declaredReferences: List<String>
    get() = when (this) {
        is MessageNode -> this.nestedNodes.flatMap { it.declaredReferences } + this.qualifiedName
        is EnumNode -> listOf(this.qualifiedName)
        is ObjectNode -> emptyList()
    }

fun interface ImportResolver {
    // Return file to import
    fun resolve(protobufName: String): String
}

private fun Node.allNodes(): List<Node> {
    return this.nestedNodes.flatMap {
        it.allNodes()
    } + this
}

fun computeImports(
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
        .mapNotNull { TypeResolver.qualifiedNameToProtobufName[it] }
        .map { importResolver.resolve(it) }
        .distinct()
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
                this.inlineOf?.resolvedExternalTypes() ?: listOf(this.name)
            }
        }
    }
}