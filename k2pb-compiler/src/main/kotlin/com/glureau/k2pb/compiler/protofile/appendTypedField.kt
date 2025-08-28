package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.appendNullabilityField

fun StringBuilder.appendTypedField(indentLevel: Int, field: TypedField) {
    appendComment(indentLevel, field.comment)

    append(indentation(indentLevel))
    appendFieldType(field.type, field.annotatedConverter)
    append(" ")
    append(field.resolvedName)
    append(" = ")
    append(field.protoNumber)
    appendLine(";")
}
