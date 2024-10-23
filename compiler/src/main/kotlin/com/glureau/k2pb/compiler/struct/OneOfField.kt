package com.glureau.k2pb.compiler.struct

import com.squareup.kotlinpoet.FunSpec

data class OneOfField(
    override val comment: String?,
    override val name: String,
    override val protoNumber: Int,
    val fields: List<FieldInterface>,
) : FieldInterface

fun StringBuilder.appendOneOfField(indentLevel: Int, field: OneOfField, numberManager: NumberManager) {
    appendLineWithIndent(indentLevel, "oneof ${field.name} {")
    field.fields.forEach { subclass ->
        appendField(indentLevel + 1, subclass, numberManager)
    }
    appendLineWithIndent(indentLevel, "}")
}

fun FunSpec.Builder.encodeOneOfField(field: OneOfField) {
    TODO()
}

fun FunSpec.Builder.decodeOneOfField(field: OneOfField) {
    TODO()
}

fun FunSpec.Builder.decodeOneOfFieldVariableDefinition(field: OneOfField) {
    TODO()
}