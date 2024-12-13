package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.TypedField

fun StringBuilder.appendTypedField(indentLevel: Int, field: TypedField) {
    appendComment(indentLevel, field.comment)

    append(indentation(indentLevel))
    appendFieldType(field.type, field.annotatedConverter)
    append(" ")
    append(field.resolvedName)
    append(" = ")
    append(field.protoNumber)
    appendLine(";")

    if (field.nullabilitySubField != null) {
        append(indentation(indentLevel))
        appendFieldType(ScalarFieldType.Boolean, null)
        append(" ")
        append(field.nullabilitySubField.fieldName)
        append(" = ")
        append(field.nullabilitySubField.protoNumber)
        appendLine(";")
    }
}