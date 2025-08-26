package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.ExplicitNullability
import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.decapitalizeUS
import com.glureau.k2pb.compiler.struct.DeprecatedField
import com.glureau.k2pb.compiler.struct.FieldInterface
import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.nullabilityQualifiedName
import kotlin.math.max

fun StringBuilder.appendFields(
    indentLevel: Int,
    activeFields: List<FieldInterface>,
    deprecatedFields: List<DeprecatedField>,
    debugName: String,
) {
    val maxProtoNumber = max(
        deprecatedFields.maxOfOrNull { it.protoNumber } ?: 1,
        activeFields.maxOfOrNull { it.protoNumber } ?: 1,
    )

    for (index in 1..maxProtoNumber) {
        val activeField = activeFields.firstOrNull { it.protoNumber == index }
        val activeNullabilityField = activeFields.filterIsInstance<TypedField>()
            .mapNotNull { it.nullabilitySubField }
            .firstOrNull { it.protoNumber == index }
        val deprecatedField = deprecatedFields.firstOrNull { it.protoNumber == index }
        when {
            activeField != null && deprecatedField != null ->
                Logger.error("Conflict, the protoNumber $index is used by both active and deprecated fields in $debugName")

            activeField == null && deprecatedField == null && activeNullabilityField == null -> {
                activeFields.filterIsInstance<TypedField>()
                    .map { it.nullabilitySubField?.protoNumber }
                val message =
                    "The protoNumber $index is not defined, if it's not used anymore consider using deprecated annotation."
                appendLineWithIndent(indentLevel, "// $message")
                Logger.warn(message)
            }

            activeField != null ->
                appendField(indentLevel, activeField)

            deprecatedField != null -> {
                if (deprecatedField.publishedInProto) {
                    appendLineWithIndent(
                        indentLevel,
                        "// Deprecated field ${deprecatedField.protoName} ${deprecatedField.protoNumber}"
                    )
                    deprecatedField.deprecationReason?.let { reason ->
                        appendLineWithIndent(
                            indentLevel,
                            "// $reason"
                        )
                    }
                    val protoType = when (val it = deprecatedField.protoType) {
                        ExplicitNullability.PROTO_TYPE -> nullabilityQualifiedName
                        else -> it
                    }
                    val protoName = deprecatedField.protoName.substringAfterLast(".").decapitalizeUS()
                    appendLineWithIndent(
                        indentLevel,
                        "$protoType $protoName = ${deprecatedField.protoNumber};"
                    )
                } else {
                    // Reserved field needs to be defined before the oneof {}
                }
            }
        }
    }
}