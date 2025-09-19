package com.glureau.k2pb.compiler.poet

import com.glureau.k2pb.DelegateProtoCodec
import com.glureau.k2pb.ExplicitNullability
import com.glureau.k2pb.compiler.mapping.nullabilityNameForField
import com.glureau.k2pb.compiler.struct.DeprecatedField
import com.glureau.k2pb.compiler.struct.DeprecatedNullabilityField
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.OneOfField
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.asClassName
import com.glureau.k2pb.compiler.struct.buildNullable
import com.glureau.k2pb.compiler.struct.decodeField
import com.glureau.k2pb.compiler.struct.decodeFieldVariableDefinition
import com.glureau.k2pb.compiler.struct.decodeNullability
import com.glureau.k2pb.compiler.struct.encodeField
import com.glureau.k2pb.compiler.struct.encodeNullability
import com.glureau.k2pb.compiler.struct.nameOrDefault
import com.glureau.k2pb.compiler.struct.sortFields
import com.squareup.kotlinpoet.FunSpec

fun FunSpec.Builder.generateDataClassCodecEncode(
    messageNode: MessageNode,
    instanceName: String,
): FunSpec.Builder {
    addStatement("// If $instanceName is null, nothing to encode")
    addStatement("if ($instanceName == null) return")

    sortFields(
        location = messageNode.name,
        activeFields = messageNode.fields,
        deprecatedFields = messageNode.deprecatedFields,
        onActiveField = { activeField ->
            addStatement("")
            addStatement("// Encode ${activeField.name}")
            encodeField(instanceName, activeField)
        },
        onDeprecatedField = { deprecatedField ->
            // Preserve retrocompat by always encoding those values...
            val target = messageNode.fields
                .firstOrNull { nullabilityNameForField(it.name) == deprecatedField.protoName }
            val encodeValue = when (target) {
                is TypedField -> {
                    // In this case, the nullability field has been removed, the target field is still here.
                    // So for retrocompat, we need to encode NOT_NULL
                    if (target.nullabilitySubField == null) ExplicitNullability.NOT_NULL
                    else ExplicitNullability.UNKNOWN
                }

                is OneOfField -> TODO("Not supported yet")
                null -> {
                    // It's possible that both the target and the nullability field have been removed.
                    // If the target field is still published in the proto, then we need to encode NULL,
                    // otherwise we can skip it.
                    val deprecatedTarget = messageNode.deprecatedFields
                        .firstOrNull { nullabilityNameForField(it.protoName) == deprecatedField.protoName }
                    if (deprecatedTarget?.publishedInProto == true) {
                        // Here we assume that if the data is reserved, it's not on the wire anymore,
                        // so NULL looks reasonable enough for a retrocompat encoding.
                        ExplicitNullability.NULL
                    } else {
                        null
                    }
                }
            }
            when (encodeValue) {
                ExplicitNullability.NULL -> {
                    addComment("Deprecated nullability field are still encoded for retrocompat")
                    encodeNullability(deprecatedField.protoNumber, isNull = true)
                }

                ExplicitNullability.NOT_NULL -> {
                    addComment("Deprecated nullability field are still encoded for retrocompat")
                    encodeNullability(deprecatedField.protoNumber, isNull = false)
                }

                ExplicitNullability.UNKNOWN,
                null,
                    -> Unit
            }
        },
        onActiveNullabilityField = { nullabilityField, targetField ->
            addStatement("// Encode nullability for ${targetField.name}")
            val fieldName = if (targetField.type is ReferenceType && targetField.type.inlineName != null) {
                "$instanceName.${targetField.name}?.${targetField.type.inlineName}"
            } else {
                "$instanceName.${targetField.name}"
            }
            beginControlFlow("if ($fieldName != null)")
            encodeNullability(nullabilityField.protoNumber, false)
            nextControlFlow("else")
            encodeNullability(nullabilityField.protoNumber, true)
            endControlFlow()
        },
        onUnusedProtoNumber = { /* Ignored */ }
    )
    return this
}

fun FunSpec.Builder.generateDataClassCodecDecode(
    messageNode: MessageNode,
    instanceName: String,
    protoCodecName: String,
): FunSpec.Builder {
    messageNode.fields.forEach {
        decodeFieldVariableDefinition(it)
    }
    addStatement("")

    if (messageNode.isInlineClass) {
        return this
    }

    beginControlFlow("while (!eof)")
    beginControlFlow("when (val tag = readTag())")

    sortFields(
        location = messageNode.name,
        activeFields = messageNode.fields,
        deprecatedFields = messageNode.deprecatedFields,
        onActiveField = { f ->
            beginControlFlow("${f.protoNumber} ->")
            decodeField(f)
            endControlFlow()
        },
        onDeprecatedField = { f ->
            beginControlFlow("${f.protoNumber} ->")
            addComment("Deprecated field ${f.protoName}, need to be consumed but value is ignored.")
            when (f) {
                is DeprecatedField -> {
                    addComment("Any ignored message can be interpreted as a byte array")
                }

                is DeprecatedNullabilityField -> {
                    addComment("Nullability field are encoded/decoded as int")
                }
            }
            addStatement("skipElement()")// new line (code readability)

            endControlFlow()
        },
        onActiveNullabilityField = { f, t ->
            beginControlFlow("${f.protoNumber} ->")
            decodeNullability(f)
            endControlFlow()
        },
        onUnusedProtoNumber = {},
    )

    beginControlFlow("else ->")
    addStatement("// Ignore unknown tag, basic forward compatibility")
    addStatement("$protoCodecName.${DelegateProtoCodec::onUnknownProtoNumber.name}(${messageNode.name}::class, tag)")
    addStatement("skipElement()")
    endControlFlow()

    endControlFlow() // when (tag)
    endControlFlow() // while (!eof)

    addStatement("return %T(", messageNode.asClassName())
    messageNode.fields.forEach {
        when (it) {
            is OneOfField -> {
                // TODO()
            }

            is TypedField -> {
                val str = if ((it.type is ReferenceType) && it.type.isNullable == false) {
                    when {
                        it.type.inlineOf?.isNullable == true -> {
                            "${it.name} ?: ${it.type.className}(null),"
                        }

                        it.type.isEnum -> {
                            "${it.name} ?: ${it.type.enumFirstEntry},"
                        }

                        (it.type.inlineOf is ReferenceType) && it.type.inlineOf.isEnum -> {
                            "${it.name} ?: ${it.type.className}(${it.type.inlineOf.enumFirstEntry}),"
                        }

                        (it.type.inlineOf is ScalarFieldType) -> {
                            "${it.name} ?: ${it.type.className}(${it.type.inlineOf.defaultValue}),"
                        }

                        else -> {
                            "requireNotNull(${it.name}),"
                        }
                    }
                } else {
                    if (it.nullabilitySubField != null) {
                        val nameOrDefault = when {
                            it.type is ReferenceType && it.type.inlineOf is ScalarFieldType -> {
                                "(${it.name} ?: ${it.type.className.simpleName}(${it.type.inlineOf.defaultValue}))"
                            }
                            /*

                            it.type is ReferenceType -> {
                                it.name
                            }*/

                            else -> {
                                it.nameOrDefault()
                            }
                        }
                        buildNullable(
                            nullabilitySubField = it.nullabilitySubField,
                            nameOrDefault = nameOrDefault,
                        ) + ","
                    } else {
                        "${it.nameOrDefault()},"
                    }
                }
                addStatement("  ${it.name} = " + str)
            }
        }
    }
    addStatement(")")
    return this
}
