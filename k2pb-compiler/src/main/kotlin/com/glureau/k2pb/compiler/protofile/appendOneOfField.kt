package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.decapitalizeUS
import com.glureau.k2pb.compiler.struct.OneOfField

fun StringBuilder.appendOneOfField(indentLevel: Int, field: OneOfField) {
    appendReservedFields(indentLevel, field.deprecatedFields)
    appendLineWithIndent(indentLevel, "oneof ${field.name.substringAfterLast(".").decapitalizeUS()} {")
    appendFields(
        indentLevel = indentLevel + 1,
        activeFields = field.activeFields,
        deprecatedFields = field.deprecatedFields,
        debugName = field.name,
    )
    appendLineWithIndent(indentLevel, "}")
}
