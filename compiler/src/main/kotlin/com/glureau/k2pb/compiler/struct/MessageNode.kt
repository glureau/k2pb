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

fun FileSpec.Builder.addMessageNote(messageNode: MessageNode) {
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
                        if (messageNode.isPolymorphic) return@apply // TODO: Handle polymorphic types
                        messageNode.fields.forEach {
                            it as TypedField
                            val tag = it.protoNumber
                            when (it.type) {
                                is ListType -> this
                                is MapType -> this
                                is ReferenceType -> {
                                    beginControlFlow("%M($tag) {", writeMessageExt)
                                    beginControlFlow("with(delegate) {")
                                    addStatement("encode(instance.${it.name}, ${it.type.name}::class)")
                                    endControlFlow()
                                    endControlFlow()
                                }

                                is ScalarFieldType -> {
                                    (it.annotatedSerializer?.let { s ->
                                        addStatement(
                                            "val ${it.name}Encoded = %T().encode(instance.${it.name})",
                                            s.toClassName()
                                        )
                                        addCode(it.type.writeMethod("${it.name}Encoded", tag))
                                    } ?: addCode(it.type.writeMethod("instance.${it.name}", tag)))
                                        .also { addStatement("") }
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
                        messageNode.fields.forEach {
                            val typeName = when (it) {
                                is OneOfField -> TODO()
                                is TypedField -> when (it.type) {
                                    is ListType -> "List<*>"
                                    is MapType -> "Map<*>"
                                    is ReferenceType -> it.type.name
                                    is ScalarFieldType -> it.type.kotlinClass.simpleName
                                }
                            }
                            addStatement("var ${it.name}: ${typeName}? = null")
                        }
                        addStatement("")
                        beginControlFlow("while (!eof)")
                        addStatement("val tag = readTag()")
                        beginControlFlow("when (tag)")
                        messageNode.fields.forEach { f ->
                            when (f) {
                                is OneOfField -> TODO()
                                is TypedField -> when (f.type) {
                                    is ListType -> Unit
                                    is MapType -> Unit
                                    is ReferenceType -> {
                                        beginControlFlow("${f.protoNumber} -> {")
                                        beginControlFlow("${f.name} = with(delegate) {")
                                        addStatement("decode(${f.type.name}::class)")
                                        endControlFlow()
                                        endControlFlow()
                                    }

                                    is ScalarFieldType -> {
                                        addStatement("${f.protoNumber} -> ${f.name} = ${f.type.readMethod()}\n")
                                    }
                                }
                            }
                            //addStatement("${f.protoNumber} -> ${f.name} = readString()")
                        }
                        // addStatement("1 -> eventUUID = readString()")
                        // addStatement("2 -> bytes = readByteArray()")
                        beginControlFlow("else -> ")
                        //addStatement("pushBackTag()") // ???
                        // TODO: Incomplete implementation, ignoring a tag should also ignore the next data...
                        addStatement("// Ignore unknown tag??")
                        addStatement("error(\"Ignoring unknown tag: \$tag\")")
                        endControlFlow()
                        //addStatement("")
                        //addStatement("}") // else block, endControlFlow can't be used here
                        endControlFlow() // when (tag)
                        endControlFlow() // while (!eof)
                        addStatement("return %T(", className)
                        messageNode.fields.forEach {
                            val protoDefaultValue: (name: String) -> String = when (it) {
                                is OneOfField -> {
                                    TODO()
                                }

                                is TypedField -> when (it.type) {
                                    is ListType -> { n -> "$n ?: emptyList()" }
                                    is MapType -> { n -> "$n ?: emptyMap()" }
                                    is ReferenceType -> { n -> if (it.type.isNullable) "$n ?: null" else "$n!!" }
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
                            }
                            when (it) {
                                is TypedField -> {
                                    if (it.annotatedSerializer != null) {
                                        addStatement("${it.name} = %T().decode(${protoDefaultValue(it.name)})", it.annotatedSerializer.toClassName())
                                    } else {
                                        addStatement("${it.name} = ${protoDefaultValue(it.name)},")
                                    }
                                }

                                else -> {
                                    TODO()
                                }
                            }
                            //addStatement("${it.name} = ${protoDefaultValue(it.name)},")
                        }
                        addStatement(")")
                    }
                    .build()
            )
            .build()
    )
}