package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.DelegateProtoCodec
import com.glureau.k2pb.ProtoCodec
import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.ProtobufWriter
import com.glureau.k2pb.compiler.struct.Node
import com.glureau.k2pb.compiler.struct.asClassName
import com.glureau.k2pb.compiler.struct.codecClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName

fun FileSpec.Builder.generateCodecType(
    node: Node,
    encodeContent: FunSpec.Builder.(instanceName: String, protoCodecName: String) -> FunSpec.Builder,
    decodeContent: FunSpec.Builder.(instanceName: String, protoCodecName: String) -> FunSpec.Builder,
) {
    addFileComment("Generated from ${node.originalFile?.filePath}")
    val className = node.asClassName()
    val instanceName = "instance"
    val protoCodecName = "protoCodec"
    addType(
        TypeSpec
            .classBuilder(node.codecClassName())
            .addModifiers(KModifier.INTERNAL)
            .addSuperinterface(ProtoCodec::class.asClassName().parameterizedBy(className))
            .addFunction(
                FunSpec.builder("encode")
                    .receiver(ProtobufWriter::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(instanceName, className.copy(nullable = true))
                    .addParameter(protoCodecName, DelegateProtoCodec::class.asClassName())
                    .encodeContent(instanceName, protoCodecName)
                    .build()
            )

            .addFunction(
                FunSpec.builder("decode")
                    .receiver(ProtobufReader::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter(protoCodecName, DelegateProtoCodec::class.asClassName())
                    .returns(className.copy(nullable = true))
                    .decodeContent(instanceName, protoCodecName)
                    .build()
            )
            .build()
    )
}