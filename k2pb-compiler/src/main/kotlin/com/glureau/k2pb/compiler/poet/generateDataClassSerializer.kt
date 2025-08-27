package com.glureau.k2pb.compiler.poet

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
import com.glureau.k2pb.compiler.struct.nullabilityClass
import com.squareup.kotlinpoet.FunSpec

fun FunSpec.Builder.generateDataClassCodecEncode(
    messageNode: MessageNode,
    instanceName: String,
): FunSpec.Builder {
    addStatement("// If $instanceName is null, nothing to encode")
    addStatement("if ($instanceName == null) return")

    messageNode.fields.forEach {
        addStatement("")
        addStatement("// Encode ${it.name}")
        encodeField(instanceName, it)
    }

    val deprecatedFields = messageNode.deprecatedFields.filter { it.protoType == nullabilityClass }
    if (deprecatedFields.isNotEmpty()) {
        addStatement("")
        addStatement("// ---------- Deprecated fields ----------")
        addStatement("")

        deprecatedFields
            .forEach { deprecatedNullableField ->
                // Preserve retrocompat by always encoding those values...
                val target = messageNode.fields
                    .firstOrNull { nullabilityNameForField(it.name) == deprecatedNullableField.protoName }
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
                            .firstOrNull { nullabilityNameForField(it.protoName) == deprecatedNullableField.protoName }
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
                    ExplicitNullability.NULL -> encodeNullability(deprecatedNullableField.protoNumber, isNull = true)
                    ExplicitNullability.NOT_NULL -> encodeNullability(
                        deprecatedNullableField.protoNumber,
                        isNull = false
                    )

                    ExplicitNullability.UNKNOWN,
                    null,
                        -> Unit
                }
            }
    }
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
    messageNode.fields.forEach { f ->
        beginControlFlow("${f.protoNumber} ->")
        decodeField(f)
        endControlFlow()
        if (f is TypedField && f.nullabilitySubField != null) {
            beginControlFlow("${f.nullabilitySubField.protoNumber} ->")
            decodeNullability(f.nullabilitySubField)
            endControlFlow()
        }
    }
    if (messageNode.deprecatedFields.isNotEmpty()) {
        addStatement("")
        addStatement("// ---------- Deprecated fields ----------")
        addStatement("")
        messageNode.deprecatedFields.sortedBy { it.protoNumber }.forEach { f ->
            beginControlFlow("${f.protoNumber} ->")
            addComment("Deprecated field ${f.protoName}, need to be consumed but value is ignored.")
            when (f) {
                is DeprecatedField -> {
                    addComment("Any ignored message can be interpreted as a byte array")
                    // TODO: deprecation still need to consume fully the message.
                    //  problem here, the type is unknown, so we cannot consume it properly.
                    //  if it's a scalar type
                    // addCode(ScalarFieldType.ByteArray.readMethodNoTag())
                }

                is DeprecatedNullabilityField -> {
                    addComment("Nullability field are encoded/decoded as int")
                    // addCode(ScalarFieldType.Int.readMethodNoTag())
                    //addStatement("")// new line (code readability)
                }
            }
            addStatement("skipElement()")// new line (code readability)

            endControlFlow()
        }
    }

    beginControlFlow("else ->")
    //addStatement("pushBackTag()") // ???
    // TODO: Incomplete implementation, ignoring a tag should also ignore the next data...
    addStatement("// Ignore unknown tag??")
    addStatement("error(\"Ignoring unknown tag: \$tag\")")
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
                            "${it.name} ?: ${it.type.className}(null), /* C */"
                        }

                        it.type.isEnum -> {
                            "${it.name} ?: ${it.type.enumFirstEntry}, /* EE */"
                        }

                        (it.type.inlineOf is ReferenceType) && it.type.inlineOf.isEnum -> {
                            "${it.name} ?: ${it.type.className}(${it.type.inlineOf.enumFirstEntry}), /* ED */"
                        }

                        (it.type.inlineOf is ScalarFieldType) -> {
                            "${it.name} ?: ${it.type.className}(${it.type.inlineOf.defaultValue}), /* E */"
                        }

                        else -> {
                            "requireNotNull(${it.name}), /* D */"
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
                        "${it.nameOrDefault()}, /* EKL */"
                    }
                }
                addStatement("  ${it.name} = " + str)
            }
        }
    }
    addStatement(")")
    return this
}
