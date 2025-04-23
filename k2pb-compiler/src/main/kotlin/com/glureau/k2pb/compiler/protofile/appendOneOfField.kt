package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.decapitalizeUS
import com.glureau.k2pb.compiler.struct.NumberManager
import com.glureau.k2pb.compiler.struct.OneOfField
import kotlin.math.max

fun StringBuilder.appendOneOfField(indentLevel: Int, field: OneOfField, numberManager: NumberManager) {
    field.deprecatedFields.filter { !it.publishedInProto }.forEach { deprecatedField ->
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
    appendLineWithIndent(indentLevel, "oneof ${field.name.substringAfterLast(".").decapitalizeUS()} {")
    val maxProtoNumber = max(
        field.deprecatedFields.maxOfOrNull { it.protoNumber } ?: 1,
        field.activeFields.maxOfOrNull { it.protoNumber } ?: 1,
    )

    for (index in 1..maxProtoNumber) {
        val activeField = field.activeFields.firstOrNull { it.protoNumber == index }
        val deprecatedField = field.deprecatedFields.firstOrNull { it.protoNumber == index }
        when {
            activeField != null && deprecatedField != null ->
                error("Conflict, the protoNumber $index is used by both active and deprecated fields")

            activeField == null && deprecatedField == null -> {
                val message =
                    "The protoNumber $index is not defined, if it's not used anymore consider using @ProtoPolymorphism.Deprecated annotation."
                appendLineWithIndent(indentLevel + 1, "// $message")
                Logger.warn(message)
            }

            activeField != null ->
                appendField(indentLevel + 1, activeField, numberManager)

            deprecatedField != null -> {
                if (deprecatedField.publishedInProto) {
                    appendLineWithIndent(
                        indentLevel + 1,
                        "// Deprecated field ${deprecatedField.protoName} ${deprecatedField.protoNumber}"
                    )
                    deprecatedField.deprecationReason?.let { reason ->
                        appendLineWithIndent(
                            indentLevel + 1,
                            "// $reason"
                        )
                    }
                    appendLineWithIndent(
                        indentLevel + 1,
                        "${deprecatedField.protoName} ${
                            deprecatedField.protoName.substringAfterLast(".").decapitalizeUS()
                        } = ${deprecatedField.protoNumber};"
                    )
                } else {
                    // Reserved field needs to be defined before the oneof {}
                }
            }
        }
    }
    appendLineWithIndent(indentLevel, "}")
}
