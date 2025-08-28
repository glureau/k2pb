package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.ExplicitNullability
import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.decapitalizeUS
import com.glureau.k2pb.compiler.struct.FieldInterface
import com.glureau.k2pb.compiler.struct.IDeprecatedField
import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.appendNullabilityField
import com.glureau.k2pb.compiler.struct.nullabilityQualifiedName
import com.glureau.k2pb.compiler.struct.sortFields

fun StringBuilder.appendFields(
    indentLevel: Int,
    activeFields: List<FieldInterface>,
    deprecatedFields: List<IDeprecatedField>,
    debugName: String,
) {
    sortFields(
        location = debugName,
        activeFields = activeFields,
        deprecatedFields = deprecatedFields,
        onActiveField = { activeField -> appendField(indentLevel, activeField) },
        onDeprecatedField = { deprecatedField ->
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
        },
        onActiveNullabilityField = { nullabilityField, _ ->
            append(indentation(indentLevel))
            appendNullabilityField(nullabilityField)
        },
        onUnusedProtoNumber = { index ->
            activeFields.filterIsInstance<TypedField>()
                .map { it.nullabilitySubField?.protoNumber }
            val message =
                "The protoNumber $index is not defined, if it's not used anymore consider using deprecated annotation."
            appendComment(indentLevel, message)
            Logger.warn(message)
        }
    )
}