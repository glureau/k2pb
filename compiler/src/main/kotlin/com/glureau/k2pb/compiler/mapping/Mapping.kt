package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.*
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Modifier

fun ProtobufAggregator.recordKSClassDeclaration(declaration: KSClassDeclaration) {
    when (declaration.classKind) {
        ClassKind.INTERFACE -> TODO()
        ClassKind.CLASS -> declaration.toProtobufMessageNode()?.let { recordMessageNode(it) }
        ClassKind.ENUM_CLASS -> recordEnumNode(declaration.toProtobufEnumNode())
        ClassKind.ENUM_ENTRY -> TODO()
        ClassKind.OBJECT -> TODO()
        ClassKind.ANNOTATION_CLASS -> error("Cannot serialize annotation class")
    }
}

private fun KSClassDeclaration.toProtobufEnumNode(): EnumNode {
    val entries = declarations.toList()
        .filterIsInstance<KSClassDeclaration>()
        .filter { it.classKind == ClassKind.ENUM_ENTRY }
        .mapIndexed { index, entry ->
            EnumEntry(
                name = entry.simpleName.asString(),
                comment = entry.docString,
                // TODO: handle annotations for name and number
                number = index, // proto3: enum starts at 0
            )
        }
    return EnumNode(
        qualifiedName = qualifiedName!!.asString(),
        name = protobufName(),
        comment = docString,
        entries = entries,
        originalFile = containingFile,
    )
}

private fun KSClassDeclaration.toProtobufMessageNode(): MessageNode? {
    // Abstract and sealed classes does NOT require a 'message' in protobuf
    // also having a nesting in a sealed/abstract could be problematic...
    if (this.modifiers.contains(Modifier.ABSTRACT)) {
        return null
    }
    if (this.modifiers.contains(Modifier.SEALED)) {
        OneOfRecorder.recordSealedClass(
            qualifiedName!!.asString(),
            getSealedSubclasses().map { it.qualifiedName!!.asString() }.toList()
        )
        // This is required to handle nested sealed classes
        // TODO: could be improved if we create it ONLY when there's nesting
        return MessageNode(
            qualifiedName = this.qualifiedName!!.asString(),
            name = protobufName(),
            comment = docString,
            fields = emptyList(),
            originalFile = containingFile
        )
    }
    // TODO: This is handling "data class", not sure about "class" and other kinds (sealed class, etc)
    val fields = primaryConstructor!!.parameters.mapIndexed { index, param ->
        val prop = this.getDeclaredProperties().first { it.simpleName == param.name }

        val resolvedType = param.type.resolve()
        if (resolvedType.declaration.modifiers.contains(Modifier.SEALED)) {
            OneOfField(
                name = param.name!!.asString(), // TODO annotation SerialName
                comment = prop.docString,
                fields = (resolvedType.declaration as KSClassDeclaration).getSealedSubclasses()
                    .mapIndexed { index, subclass ->
                        // TODO: Handle oneOf recursively
                        TypedField(
                            name = subclass.simpleName.asString(), // TODO annotation SerialName
                            type = subclass.asType(emptyList()).toProtobufFieldType(),
                            comment = subclass.docString,
                            number = index + 1, // TODO annotation + local increment
                        )
                    }.toList(),
            )
        } else {
            TypedField(
                name = param.name!!.asString(), // TODO annotation SerialName
                type = resolvedType.toProtobufFieldType(),
                comment = prop.docString,
                number = index + 1, // TODO annotation + local increment
            )
        }
    }
    return MessageNode(
        qualifiedName = this.qualifiedName!!.asString(),
        name = protobufName(),
        comment = docString,
        fields = fields,
        originalFile = containingFile
    )
}

private fun KSType.toProtobufFieldType(): FieldType {
    return when (val name = this.declaration.qualifiedName!!.asString()) {
        "kotlin.String" -> ScalarType.string
        "kotlin.Int" -> ScalarType.int32
        "kotlin.Char" -> ScalarType.int32
        "kotlin.Short" -> ScalarType.int32
        "kotlin.Byte" -> ScalarType.int32
        "kotlin.Long" -> ScalarType.int64
        "kotlin.Float" -> ScalarType.float
        "kotlin.Double" -> ScalarType.double
        "kotlin.Boolean" -> ScalarType.bool
        "kotlin.collections.List" -> ListType(
            repeatedType = arguments[0].type!!.resolve().toProtobufFieldType() // TODO: List<List<Int>> is not supported
        )

        "kotlin.collections.Map" -> MapType(
            keyType = arguments[0].type!!.resolve().toProtobufFieldType(), // TODO: Map<Map<X, X>, X> is not supported
            valueType = arguments[1].type!!.resolve().toProtobufFieldType(),
        )

        else -> ReferenceType(name)
    }
}


fun KSClassDeclaration.protobufName(): String {
    val p = parent as? KSClassDeclaration
    return ((if (p != null) p.protobufName() + "." else null) ?: "") +
            simpleName.asString()
}
