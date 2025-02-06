package com.glureau.k2pb.compiler.poet

import com.glureau.k2pb.compiler.struct.FieldType
import com.glureau.k2pb.compiler.struct.ListType
import com.glureau.k2pb.compiler.struct.MapType
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.OneOfField
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.asClassName
import com.glureau.k2pb.compiler.struct.decodeField
import com.glureau.k2pb.compiler.struct.decodeFieldVariableDefinition
import com.glureau.k2pb.compiler.struct.decodeScalarType
import com.glureau.k2pb.compiler.struct.encodeField
import com.glureau.k2pb.compiler.struct.nameOrDefault
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

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
            decodeScalarType(f.nullabilitySubField.fieldName, ScalarFieldType.Boolean, null)
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

    addStatement("return DefaultConstructor(")
    messageNode.fields.forEach { f ->
        addStatement("${f.name} = ${f.name},")
        if (f is TypedField && f.nullabilitySubField != null) {
            addStatement("${f.nullabilitySubField.fieldName} = ${f.nullabilitySubField.fieldName},")
        }
    }
    addStatement(")")
    return this
}

fun TypeSpec.Builder.addConstructorTypes(node: MessageNode, className: ClassName, serializerClassName: ClassName) {
    if (node.isPolymorphic) return // TODO: Handle polymorphic for retrocompat
    this.addType(
        TypeSpec.interfaceBuilder("Constructor")
            .addFunction(
                buildInvokeFunction(node, className)
                    .addModifiers(KModifier.ABSTRACT)
                    .build()
            )
            .build()
    )
        .addType(
            TypeSpec.objectBuilder("DefaultConstructor")
                .addModifiers(KModifier.PRIVATE)
                .addSuperinterface(
                    ClassName(
                        serializerClassName.packageName,
                        serializerClassName.simpleName,
                        "Constructor"
                    )
                )
                .addFunction(
                    buildInvokeFunction(node, className)
                        .addModifiers(KModifier.OVERRIDE)
                        .addStatement("// Patch KotlinPoet")
                        .returnBuildIt(node)
                        .build()
                )
                .build()
        )
}

private fun buildInvokeFunction(messageNode: MessageNode, className: ClassName) =
    FunSpec.builder("invoke")
        .addModifiers(KModifier.OPERATOR)
        .also { invokeFunc ->
            messageNode.fields.forEach { f ->
                when (f) {
                    is OneOfField -> {}
                    is TypedField -> {
                        invokeFunc.addParameter(
                            name = f.name,
                            type = f.type.typeName.copy(nullable = f.type !is ListType && f.type !is MapType)
                        )
                        if (f.nullabilitySubField != null) {
                            invokeFunc.addParameter(
                                name = f.nullabilitySubField.fieldName,
                                type = BOOLEAN
                            )
                        }
                    }
                }
            }
        }
        .returns(className.copy(nullable = true))


private fun FunSpec.Builder.returnBuildIt(messageNode: MessageNode): FunSpec.Builder = this
    .addStatement("return %T(", messageNode.asClassName())
    .also {
        messageNode.fields.forEach {
            when (it) {
                is OneOfField -> {
                    // TODO()
                }

                is TypedField -> {
                    val str = if ((it.type is ReferenceType) && it.type.isNullable == false) {
                        when {
                            it.type.inlineOf?.isNullable == true -> {
                                "  ${it.name} = ${it.name} ?: ${it.type.typeName}(null), /* C */"
                            }

                            it.type.isEnum -> {
                                "  ${it.name} = ${it.name} ?: ${it.type.enumFirstEntry}, /* EE */"
                            }

                            (it.type.inlineOf is ReferenceType) && it.type.inlineOf.isEnum -> {
                                "  ${it.name} = ${it.name} ?: ${it.type.typeName}(${it.type.inlineOf.enumFirstEntry}), /* ED */"
                            }

                            (it.type.inlineOf is ScalarFieldType) -> {
                                "  ${it.name} = ${it.name} ?: ${it.type.typeName}(${it.type.inlineOf.defaultValue}), /* E */"
                            }

                            else -> {
                                "  ${it.name} = requireNotNull(${it.name}), /* D */"
                            }
                        }
                    } else {
                        if (it.nullabilitySubField != null) {
                            if (it.type is ReferenceType && it.type.inlineOf is ScalarFieldType) {
                                "  ${it.name} = if (${it.nullabilitySubField.fieldName}) null " +
                                        "else (${it.name} ?: ${it.type.name}(${it.type.inlineOf.defaultValue})), /* KP */"
                            } else if (it.type is ReferenceType) {
                                "  ${it.name} = if (${it.nullabilitySubField.fieldName}) null else requireNotNull(${it.name}), /* KL */"
                            } else {
                                "  ${it.name} = if (${it.nullabilitySubField.fieldName}) null else ${it.nameOrDefault()}, /* K */"
                            }
                        } else {
                            "  ${it.name} = ${it.nameOrDefault()}, /* E */"
                        }
                    }
                    addStatement(str)
                }
            }
        }
    }
    .addStatement(")")
