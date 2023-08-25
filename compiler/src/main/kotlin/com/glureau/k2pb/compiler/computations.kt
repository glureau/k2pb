package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.struct.*
import com.google.devtools.ksp.symbol.KSFile


val ProtobufFile.dependencies: List<KSFile>
    get() = (messages.flatMap { it.dependencies } + enums.mapNotNull { it.originalFile }).distinct()

internal val Node.declaredReferences: List<String>
    get() = when (this) {
        is MessageNode -> this.nestedNodes.flatMap { it.declaredReferences } + this.qualifiedName
        is EnumNode -> listOf(this.qualifiedName)
    }

internal val Node.allTypeReferences: List<String>
    get() = when (this) {
        is MessageNode -> fields.flatMap { it.allFieldTypes() }
            .flatMap {
                when (it) {
                    is ReferenceType -> listOf(it.name)
                    is ListType -> when (it.repeatedType) {
                        is ReferenceType -> listOf(it.repeatedType.name)
                        else -> emptyList()
                    }

                    is MapType -> {
                        when (it.keyType) {
                            is ReferenceType -> listOf(it.keyType.name)
                            else -> emptyList()
                        } + when (it.valueType) {
                            is ReferenceType -> listOf(it.valueType.name)
                            else -> emptyList()
                        }
                    }

                    else -> emptyList()
                }
            } + nestedNodes.flatMap { it.allTypeReferences }

        is EnumNode -> emptyList()
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
            messageNodes.flatMap { it.allTypeReferences } +
                    enumNodes.flatMap { it.allTypeReferences }
            ).distinct()

    return (allTypeReferences - locallyDeclaredReferences.toSet())
        .mapNotNull {
            TypeResolver.qualifiedNameToProtobufName[it] ?: run {
                // TODO: TU
                if (sharedOptions.shouldImportForReplace(it) == true) {
                    sharedOptions.replace(it)
                } else {
                    null
                }
            }
        }
        .map { importResolver.resolve(it) }
        .distinct()
}

fun FieldInterface.allFieldTypes(): List<FieldType> = when (this) {
    is TypedField -> listOf(this.type)
    is OneOfField -> this.fields.flatMap { it.allFieldTypes() }
}