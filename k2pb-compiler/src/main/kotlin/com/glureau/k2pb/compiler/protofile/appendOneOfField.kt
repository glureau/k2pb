package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.decapitalizeUS
import com.glureau.k2pb.compiler.struct.NumberManager
import com.glureau.k2pb.compiler.struct.OneOfField

fun StringBuilder.appendOneOfField(indentLevel: Int, field: OneOfField, numberManager: NumberManager) {
    appendLineWithIndent(indentLevel, "oneof ${field.name.substringAfterLast(".").decapitalizeUS()} {")
    field.fields.forEach { subclass ->
        appendField(indentLevel + 1, subclass, numberManager)
    }
    appendLineWithIndent(indentLevel, "}")
}
