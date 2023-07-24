package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.*
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType

fun ProtobufAggregator.recordKSClassDeclaration(it: KSClassDeclaration) {
    when (it.classKind) {
        ClassKind.INTERFACE -> TODO()
        ClassKind.CLASS -> recordMessageNode(it.toProtobufMessageNode())
        ClassKind.ENUM_CLASS -> recordEnumNode(it.toProtobufEnumNode())
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

private fun KSClassDeclaration.toProtobufMessageNode(): MessageNode {
    // TODO: This is handling "data class", not sure about "class" and other kinds (sealed class, etc)
    val fields = primaryConstructor!!.parameters.mapIndexed { index, param ->
        val prop = this.getDeclaredProperties().first { it.simpleName == param.name }
        Field(
            name = param.name!!.asString(), // TODO annotation SerialName
            type = param.type.resolve().toProtobufFieldType(),
            comment = prop.docString,
            number = index + 1, // TODO annotation + local increment
        )
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
