package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.*
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference

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
    println("getDeclaredProperties = " + this.getDeclaredProperties())
    return EnumNode(
        name = "TODO", comment = listOf(), entries = listOf()
    )
}

private fun KSClassDeclaration.toProtobufMessageNode(): MessageNode {
    // TODO: This is handling "data class", not sure about "class" and other kinds (sealed class, etc)
    val fields = primaryConstructor!!.parameters.mapIndexed { index, param ->
        val prop = this.getDeclaredProperties().first { it.simpleName == param.name }
        val repeated = false // TODO: resolvedType.isCollection()
        Field(
            name = param.name!!.asString(),
            type = param.type.toProtobufFieldType(),// FieldType.from(param.type.toString()),
            comment = prop.docString?.let { listOf(it) } ?: emptyList(),
            repeated = repeated,
            number = index, // TODO annotation + local increment
        )
    }
    return MessageNode(
        name = protobufName(),
        comment = this.docString?.let { listOf(it) } ?: emptyList(),
        fields = fields,
        nestedNodes = emptyList()
    )
}

private fun KSTypeReference.toProtobufFieldType(): FieldType {
    val name = resolve().declaration.qualifiedName!!.asString()
    return when (name) {
        "kotlin.String" -> ScalarType.string
        "kotlin.Int" -> ScalarType.int32
        "kotlin.Char" -> ScalarType.int32
        "kotlin.Short" -> ScalarType.int32
        "kotlin.Byte" -> ScalarType.int32
        "kotlin.Long" -> ScalarType.int64
        "kotlin.Float" -> ScalarType.float
        "kotlin.Double" -> ScalarType.double
        "kotlin.Boolean" -> ScalarType.bool
        "kotlin.collections.List" -> ReferenceType("repeated")
        else -> ReferenceType(name)
    }
}


fun KSClassDeclaration.protobufName(): String {
    val p = parent as? KSClassDeclaration
    return ((if (p != null) p.protobufName() + "." else null) ?: "") +
            simpleName.asString()
}
