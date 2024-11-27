package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.DelegateProtoSerializer
import com.glureau.k2pb.ProtoSerializer
import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.ProtobufWriter
import com.glureau.k2pb.compiler.mapping.appendComment
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

data class MessageNode(
    val packageName: String,
    override val qualifiedName: String,
    override val name: String,
    val isObject: Boolean,
    val isPolymorphic: Boolean,
    val isInlineClass: Boolean,
    val superTypes: List<ClassName>,
    val comment: String?,
    val fields: List<FieldInterface>,
    override val originalFile: KSFile?,
) : Node() {
    val numberManager = NumberManager()
    val dependencies: List<KSFile>
        get() {
            val result = mutableListOf<KSFile>()
            originalFile?.let { result.add(it) }
            nestedNodes.forEach { node ->
                node.originalFile?.let { result.add(it) }
            }
            return result
        }
    val nestedNodes: MutableList<Node> = mutableListOf()

}

fun StringBuilder.appendMessageNode(indentLevel: Int, messageNode: MessageNode) {
    appendComment(indentLevel, messageNode.comment)
    appendLineWithIndent(indentLevel, "message ${messageNode.name.substringAfterLast(".")} {")
    messageNode.fields.forEach {
        appendField(indentLevel + 1, it, messageNode.numberManager)
    }
    messageNode.nestedNodes.forEach {
        when (it) {
            is MessageNode -> appendMessageNode(indentLevel + 1, it)
            is EnumNode -> appendEnumNode(indentLevel + 1, it)
        }
    }
    appendLineWithIndent(indentLevel, "}")
}

fun MessageNode.asClassName(): ClassName = ClassName(packageName, name)
fun MessageNode.serializerClassName(): ClassName = ClassName(packageName, "${name.replace(".", "_")}Serializer")
val writeMessageExt = MemberName("com.glureau.k2pb.runtime", "writeMessage")
val readMessageExt = MemberName("com.glureau.k2pb.runtime", "readMessage")

fun FileSpec.Builder.addMessageNode(messageNode: MessageNode) {
    addFileComment("Generated from ${messageNode.originalFile?.filePath}")
    val className = messageNode.asClassName()
    addType(
        TypeSpec
            .classBuilder(messageNode.serializerClassName())
            .addSuperinterface(ProtoSerializer::class.asClassName().parameterizedBy(className))
            .addFunction(
                FunSpec.builder("encode")
                    .receiver(ProtobufWriter::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("instance", className.copy(nullable = true))
                    .addParameter("delegate", DelegateProtoSerializer::class.asClassName())
                    .apply {
                        if (messageNode.isObject) return@apply // Nothing to do, dummy class for simple implementation
                        addStatement("if (instance == null) return")
                        if (messageNode.isPolymorphic) {
                            messageNode.fields.forEach {
                                encodeField(it)
                            }
                        } else {
                            if (messageNode.isInlineClass) {
                                val inlinedField = messageNode.fields.first()
                                if (inlinedField is TypedField && inlinedField.type is ScalarFieldType) {
                                    addCode(inlinedField.type.writeMethodNoTag("instance.${inlinedField.name}"))
                                } else {
                                    addStatement("writeBytes(instance.${inlinedField.name}.encodeToByteArray()) /* $inlinedField */")
                                }
                            } else {
                                messageNode.fields.forEach {
                                    encodeField(it)
                                }
                            }
                        }
                    }
                    .build()
            )

            .addFunction(
                FunSpec.builder("decode")
                    .receiver(ProtobufReader::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("delegate", DelegateProtoSerializer::class.asClassName())
                    .returns(className.copy(nullable = true))
                    .apply {
                        if (messageNode.isObject) {
                            // Dummy class for simple implementation
                            addStatement("return %T", className)
                            return@apply
                        }
                        if (messageNode.isPolymorphic) {
                            messageNode.fields.forEach { f ->
                                decodeField(f)
                            }
                            return@apply
                        }
                        if (messageNode.isInlineClass) {
                            addCode("return %T(", className)
                            val inlinedField = messageNode.fields.first()
                            if (inlinedField is TypedField && inlinedField.type is ScalarFieldType) {
                                addCode("${inlinedField.name} = ${inlinedField.type.readMethodNoTag()}")
                            } else {
                                addCode("${inlinedField.name} = readByteArrayNoTag()")
                            }
                            addCode(")")
                            return@apply
                        }

                        messageNode.fields.forEach {
                            decodeFieldVariableDefinition(it)
                        }
                        addStatement("")

// FROM THERE
                        beginControlFlow("while (!eof)")
                        beginControlFlow("when (val tag = readTag())")
                        messageNode.fields.forEach { f ->
                            beginControlFlow("${f.protoNumber} ->")
                            decodeField(f)
                            endControlFlow()
                        }
                        beginControlFlow("else -> ")
                        //addStatement("pushBackTag()") // ???
                        // TODO: Incomplete implementation, ignoring a tag should also ignore the next data...
                        addStatement("// Ignore unknown tag??")
                        addStatement("error(\"Ignoring unknown tag: \$tag\")")
                        endControlFlow()

                        endControlFlow() // when (tag)
                        endControlFlow() // while (!eof)

                        if (messageNode.isPolymorphic) {
                            addStatement("return null")
                            return@apply
                        }

                        addStatement("return %T(", className)
                        messageNode.fields.forEach {
                            when (it) {
                                is OneOfField -> {
                                    // TODO()
                                }

                                is TypedField -> {
                                    val protoDefaultValue: (name: String) -> String = when (it.type) {
                                        is ListType -> { n -> "$n ?: emptyList()" }
                                        is MapType -> { n -> "$n ?: emptyMap()" }
                                        is ReferenceType -> { n -> if (it.type.isNullable) n else "requireNotNull($n)" }
                                        ScalarFieldType.Double -> { n -> "$n ?: 0.0" }
                                        ScalarFieldType.Float -> { n -> "$n ?: 0.0f" }
                                        ScalarFieldType.Int -> { n -> "$n ?: 0" }
                                        ScalarFieldType.Short -> { n -> "$n ?: 0" }
                                        ScalarFieldType.Char -> { n -> "$n ?: 0.toChar()" }
                                        ScalarFieldType.Long -> { n -> "$n ?: 0" }
                                        ScalarFieldType.Byte -> { n -> "$n ?: 0" }
                                        ScalarFieldType.Boolean -> { n -> "$n ?: false" }
                                        ScalarFieldType.String -> { n -> "$n ?: \"\"" }
                                        ScalarFieldType.ByteArray -> { n -> "$n ?: byteArrayOf()" }

                                        is ScalarFieldType -> {
                                            error("ScalarType not handled for protobuf serialization: ${it.type}")
                                        }
                                    }
                                    if (it.annotatedSerializer != null) {
                                        val str = if ((it.type is ReferenceType) && it.type.isNullable == false) {
                                            "  ${it.name} = requireNotNull(%T().decode(${protoDefaultValue(it.name)}))"
                                        } else {
                                            "  ${it.name} = (${protoDefaultValue(it.name)})?.let { %T().decode(it) }"
                                        }
                                        addStatement(str, it.annotatedSerializer.toClassName())
                                    } else {
                                        val str = if ((it.type is ReferenceType) && it.type.isNullable == false) {
                                            if ((it.type as ReferenceType).inlineOf?.isNullable == true) {
                                                "  ${it.name} = ${it.name} ?: ${it.type.name}(null),"
                                            } else {
                                                "  ${it.name} = requireNotNull(${it.name}),"
                                            }
                                        } else {
                                            "  ${it.name} = ${protoDefaultValue(it.name)},"
                                        }
                                        addStatement(str)
                                    }
                                }
                            }
                        }
                        addStatement(")")
// TO THERE, TO REFACTOR
                    }
                    .build()
            )
            .build()
    )
}