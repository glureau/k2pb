package com.glureau.k2pb.compiler.struct

data class OneOfField(
    override val comment: String?,
    override val name: String,
    val fields: List<FieldInterface>,
) : FieldInterface

fun StringBuilder.appendOneOfField(indentLevel: Int, field: OneOfField, numberManager: NumberManager) {
    appendLineWithIndent(indentLevel, "oneof ${field.name} {")
    field.fields.forEach { subclass ->
        appendField(indentLevel + 1, subclass, numberManager)
    }
    appendLineWithIndent(indentLevel, "}")
}