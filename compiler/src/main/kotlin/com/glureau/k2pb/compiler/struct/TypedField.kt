package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.appendComment
import com.google.devtools.ksp.symbol.KSType

data class TypedField(
    override val comment: String?,
    val type: FieldType,
    override val name: String,
    override val protoNumber: Int,
    val annotatedName: String?,
    val annotatedNumber: Int?,
    val annotatedSerializer: KSType? = null,
) : FieldInterface

fun StringBuilder.appendTypedField(indentLevel: Int, field: TypedField, numberManager: NumberManager) {
    appendComment(indentLevel, field.comment)

    append(indentation(indentLevel))
    appendFieldType(field.type)
    append(" ")
    append(field.annotatedName ?: field.name)
    append(" = ")
    append(numberManager.resolve(field.name, field.annotatedNumber))
    appendLine(";")
}