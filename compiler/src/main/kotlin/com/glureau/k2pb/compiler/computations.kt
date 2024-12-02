package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.mapping.customSerializerType
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

fun computeImports(
    messageNodes: List<MessageNode>,
    enumNodes: List<EnumNode>,
    importResolver: ImportResolver,
    locallyDeclaredReferences: List<String>,
): List<String> {
    val allTypeReferences = (
            messageNodes.flatMap { it.fields.flatMap { it.resolvedExternalTypes() } } +
                    enumNodes.flatMap { listOf(it.name) }
            ).distinct()

    Logger.warn("All type references: ${allTypeReferences}")
    Logger.warn("Local references: ${locallyDeclaredReferences}")

    return (allTypeReferences - locallyDeclaredReferences.toSet())
        .onEach { string -> Logger.warn("Resolving: $string") }
        .mapNotNull { TypeResolver.qualifiedNameToProtobufName[it] }
        .onEach { string -> Logger.warn("resolved references: $string") }
        .map { importResolver.resolve(it) }
        .distinct()
}

fun FieldInterface.resolvedExternalTypes(): List<String> {
    Logger.warn("--------------------")
    Logger.warn("RESOLVING FIELD INTERFACE $this")
    return when (this) {
        is TypedField -> {
            if (this.annotatedSerializer.customSerializerType() != null) {
                Logger.warn("RESOLVING CUSTOM STRING CONVERTER - skipping resolution")
                emptyList()
            } else {
                Logger.warn("RESOLVING - no annotated serializer ${this.annotatedSerializer}")
                this.type.resolvedExternalTypes()
            }
        }

        is OneOfField -> this.fields.flatMap { it.resolvedExternalTypes() }
    }
}

fun FieldType.resolvedExternalTypes(): List<String> {
    return when (this) {
        is ListType -> {
            Logger.warn("RESOLVING LIST OF ${this.repeatedType}")
            repeatedType.resolvedExternalTypes()
        }

        is MapType -> {
            Logger.warn("RESOLVING MAP OF ${this.keyType} => ${this.valueType}")
            keyType.resolvedExternalTypes() + valueType.resolvedExternalTypes()
        }

        is ScalarFieldType -> emptyList()
        is ReferenceType -> {
            Logger.warn("RESOLVING REFERENCE OF $this")
            if (this.inlineAnnotatedSerializer.customSerializerType() != null) {
                emptyList()
            } else {
                this.inlineOf?.resolvedExternalTypes() ?: listOf(this.name)
            }
        }
    }
}