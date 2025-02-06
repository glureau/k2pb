package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.DelegateProtoSerializer
import com.glureau.k2pb.ProtoSerializer
import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.ProtobufWriter
import com.glureau.k2pb.compiler.poet.addConstructorTypes
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.Node
import com.glureau.k2pb.compiler.struct.asClassName
import com.glureau.k2pb.compiler.struct.serializerClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName

fun FileSpec.Builder.generateSerializerType(
    node: Node,
    encodeContent: FunSpec.Builder.(instanceName: String, protoSerializerName: String) -> FunSpec.Builder,
    decodeContent: FunSpec.Builder.(instanceName: String, protoSerializerName: String) -> FunSpec.Builder,
) {
    addFileComment("Generated from ${node.originalFile?.filePath}")
    val className = node.asClassName()
    val instanceName = "instance"
    val protoSerializerName = "protoSerializer"
    val serializerClassName = node.serializerClassName()

    addType(
        TypeSpec
            .classBuilder(serializerClassName)
            .addModifiers(KModifier.INTERNAL)
            .addSuperinterface(ProtoSerializer::class.asClassName().parameterizedBy(className))
            .addFunction(
                FunSpec.builder("encode")
                    .receiver(ProtobufWriter::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(instanceName, className.copy(nullable = true))
                    .addParameter(protoSerializerName, DelegateProtoSerializer::class.asClassName())
                    .encodeContent(instanceName, protoSerializerName)
                    .build()
            )

            .addFunction(
                FunSpec.builder("decode")
                    .receiver(ProtobufReader::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(protoSerializerName, DelegateProtoSerializer::class.asClassName())
                    .returns(className.copy(nullable = true))
                    .decodeContent(instanceName, protoSerializerName)
                    .build()
            )
            .also {
                if (node is MessageNode) {
                    it.addConstructorTypes(node, className, serializerClassName)
                }
            }
            .build()
    )
}