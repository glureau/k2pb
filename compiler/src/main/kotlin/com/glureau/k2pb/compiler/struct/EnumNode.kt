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
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName

data class EnumNode(
    override val packageName: String,
    override val qualifiedName: String,
    override val name: String,
    val comment: String?,
    val entries: List<EnumEntry>,
    override val originalFile: KSFile?,
) : Node() {
    override val generatesNow: Boolean get() = true
}

fun StringBuilder.appendEnumNode(indentLevel: Int, enumNode: EnumNode) {
    appendComment(indentLevel, enumNode.comment)
    appendLineWithIndent(indentLevel, "enum ${enumNode.name.substringAfterLast(".")} {")
    enumNode.entries.forEach {
        appendEnumEntry(indentLevel + 1, it)
    }
    appendLineWithIndent(indentLevel, "}")
}
fun FileSpec.Builder.addEnumNode(enumNode: EnumNode) {
    addFileComment("Generated from ${enumNode.originalFile?.filePath}")
    val className = enumNode.asClassName()
    val instanceName = "instance"
    val protoSerializerName = "protoSerializer"
    addType(
        TypeSpec
            .classBuilder(enumNode.serializerClassName())
            .addSuperinterface(ProtoSerializer::class.asClassName().parameterizedBy(className))
            .addFunction(
                FunSpec.builder("encode")
                    .receiver(ProtobufWriter::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(instanceName, className.copy(nullable = true))
                    .addParameter(protoSerializerName, DelegateProtoSerializer::class.asClassName())
                    .apply {
                        addStatement("if ($instanceName == null) return")
                        addCode(ScalarFieldType.Int.safeWriteMethodNoTag("$instanceName.ordinal /* OR */", null))
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
                        addStatement("return %T.entries.getOrNull(%L)", enumNode.asClassName(), ScalarFieldType.Int.readMethodNoTag())
                    }
                    .build()
            )
            .build()
    )
}