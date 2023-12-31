package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.appendComment

data class TypedField(
    override val comment: String?,
    val type: FieldType,
    override val name: String,
    val annotatedNumber: Int?,
) : FieldInterface

fun StringBuilder.appendTypedField(indentLevel: Int, field: TypedField, numberManager: NumberManager) {
    appendComment(indentLevel, field.comment)

    append(indentation(indentLevel))
    appendFieldType(field.type)
    append(" ")
    append(field.name)
    append(" = ")
    append(numberManager.resolve(field.name, field.annotatedNumber))
    appendLine(";")
}