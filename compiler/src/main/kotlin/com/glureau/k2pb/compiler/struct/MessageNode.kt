package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.DelegateProtoSerializer
import com.glureau.k2pb.ProtoSerializer
import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.ProtobufWriter
import com.glureau.k2pb.compiler.mapping.appendComment
import com.glureau.k2pb.compiler.poet.generateDataClassSerializerDecode
import com.glureau.k2pb.compiler.poet.generateDataClassSerializerEncode
import com.glureau.k2pb.compiler.poet.generateInlineSerializerDecode
import com.glureau.k2pb.compiler.poet.generateInlineSerializerEncode
import com.glureau.k2pb.compiler.poet.generateObjectSerializerDecode
import com.glureau.k2pb.compiler.poet.generateObjectSerializerEncode
import com.glureau.k2pb.compiler.poet.generatePolymorphicSerializerDecode
import com.glureau.k2pb.compiler.poet.generatePolymorphicSerializerEncode
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName

data class MessageNode(
    override val packageName: String,
    override val qualifiedName: String,
    override val name: String,
    val isObject: Boolean,
    val isPolymorphic: Boolean,
    // if not sealed, the generation is done in final module
    // if sealed, the generation is done in the current module
    // see [explicitGenerationRequested]
    val isSealed: Boolean,
    val explicitGenerationRequested: Boolean,
    val isInlineClass: Boolean,
    val superTypes: List<ClassName>,
    val comment: String?,
    val fields: List<FieldInterface>,
    override val originalFile: KSFile?,
    val sealedSubClasses: List<ClassName>,
) : Node() {
    val numberManager = NumberManager()

    // If the generation is not explicitly requested, polymorphic unsealed classes are skipped,
    // as they are generated in the final module (via an explicit annotation).
    override val generatesNow: Boolean
        get() = explicitGenerationRequested ||
                !isPolymorphic ||
                isSealed

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
    messageNode.nestedNodes
        //.filter { it.generatesNow }
        .forEach {
            when (it) {
                is MessageNode -> appendMessageNode(indentLevel + 1, it)
                is EnumNode -> appendEnumNode(indentLevel + 1, it)
            }
        }
    appendLineWithIndent(indentLevel, "}")
}

fun Node.asClassName(): ClassName = ClassName(packageName, name.split("."))
fun Node.serializerClassName(): ClassName = ClassName(packageName, "${name.replace(".", "_")}Serializer")
val writeMessageExt = MemberName("com.glureau.k2pb.runtime", "writeMessage")
val readMessageExt = MemberName("com.glureau.k2pb.runtime", "readMessage")

fun FileSpec.Builder.addMessageNode(messageNode: MessageNode) {
    addFileComment("Generated from ${messageNode.originalFile?.filePath}")
    val className = messageNode.asClassName()
    val instanceName = "instance"
    val protoSerializerName = "protoSerializer"
    addType(
        TypeSpec
            .classBuilder(messageNode.serializerClassName())
            .addModifiers(KModifier.INTERNAL)
            .addSuperinterface(ProtoSerializer::class.asClassName().parameterizedBy(className))
            .addFunction(
                FunSpec.builder("encode")
                    .receiver(ProtobufWriter::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(instanceName, className.copy(nullable = true))
                    .addParameter(protoSerializerName, DelegateProtoSerializer::class.asClassName())
                    .apply {
                        when {
                            messageNode.isObject ->
                                generateObjectSerializerEncode(messageNode, instanceName, protoSerializerName)

                            messageNode.isPolymorphic ->
                                generatePolymorphicSerializerEncode(messageNode, instanceName, protoSerializerName)

                            messageNode.isInlineClass ->
                                generateInlineSerializerEncode(messageNode, instanceName, protoSerializerName)

                            else ->
                                generateDataClassSerializerEncode(messageNode, instanceName, protoSerializerName)
                        }
                    }
                    .build()
            )

            .addFunction(
                FunSpec.builder("decode")
                    .receiver(ProtobufReader::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(protoSerializerName, DelegateProtoSerializer::class.asClassName())
                    .returns(className.copy(nullable = true))
                    .apply {
                        when {
                            messageNode.isObject ->
                                generateObjectSerializerDecode(messageNode, instanceName, protoSerializerName)

                            messageNode.isPolymorphic ->
                                generatePolymorphicSerializerDecode(messageNode, instanceName, protoSerializerName)

                            messageNode.isInlineClass ->
                                generateInlineSerializerDecode(messageNode, instanceName, protoSerializerName)

                            else ->
                                generateDataClassSerializerDecode(messageNode, instanceName, protoSerializerName)
                        }
                    }
                    .build()
            )
            .build()
    )
}