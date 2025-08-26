package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.DeprecatedField

fun StringBuilder.appendReservedFields(
    indentLevel: Int,
    deprecatedFields: List<DeprecatedField>,
) {
    deprecatedFields.filter { !it.publishedInProto }.forEach { deprecatedField ->
        appendLineWithIndent(
            indentLevel,
            "// Removed field ${deprecatedField.protoName} (number=${deprecatedField.protoNumber})"
        )
        deprecatedField.deprecationReason?.let { reason ->
            appendLineWithIndent(
                indentLevel,
                "// $reason"
            )
        }
        appendLineWithIndent(
            indentLevel,
            "reserved \"${deprecatedField.protoName}\";"
        )
        appendLineWithIndent(
            indentLevel,
            "reserved ${deprecatedField.protoNumber};"
        )
        appendLine()
    }
}