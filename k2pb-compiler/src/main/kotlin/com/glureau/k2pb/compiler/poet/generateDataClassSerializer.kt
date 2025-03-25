package com.glureau.k2pb.compiler.poet

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
import com.glureau.k2pb.compiler.struct.nameOrDefault
import com.squareup.kotlinpoet.FunSpec

fun FunSpec.Builder.generateDataClassSerializerEncode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
): FunSpec.Builder {
    addStatement("// If $instanceName is null, nothing to encode")
    addStatement("if ($instanceName == null) return")

    messageNode.fields.forEach {
        addStatement("")
        addStatement("// Encode ${it.name}")
        encodeField(instanceName, it)
    }
    return this
}

fun FunSpec.Builder.generateDataClassSerializerDecode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
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
                                "(${it.name} ?: ${it.type.name}(${it.type.inlineOf.defaultValue}))"
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
