package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.codegen.generateEnumSerializerType
import com.glureau.k2pb.compiler.codegen.generateMessageSerializerType
import com.glureau.k2pb.compiler.codegen.generateObjectSerializerType
import com.glureau.k2pb.compiler.codegen.generateSerializerType
import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.ObjectNode
import com.glureau.k2pb.compiler.struct.asClassName
import com.glureau.k2pb.compiler.struct.serializerClassName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

class ProtobufSerializerProducer(private val protobufAggregator: ProtobufAggregator) {
    data class CodeFile(
        val fileSpec: FileSpec,
        val aggregating: Boolean,
    )

    fun buildFileSpecs(moduleName: String): List<CodeFile> {
        val fileSpecs = protobufAggregator.nodes
            .map {
                val builder = FileSpec.builder(it.serializerClassName())
                when (it) {
                    is MessageNode -> builder.generateMessageSerializerType(it)
                    is EnumNode -> builder.generateEnumSerializerType(it)
                    is ObjectNode -> builder.generateObjectSerializerType(it)
                }
                CodeFile(builder.build(), false)
            }

        if (fileSpecs.isEmpty()) {
            return emptyList()
        }

        val packages = protobufAggregator.nodes.map { it.packageName }
        // Find the common package
        val commonPackage = packages.reduce { acc, s -> acc.commonPrefixWith(s) }

        // sample-lib => SampleLib
        val cleanModuleName = moduleName
            .split("-")
            .joinToString("") { it.capitalizeUS() }

        val moduleCodeFile = CodeFile(
            FileSpec.builder(commonPackage, "${cleanModuleName}Serializers")
                .addFunction(
                    FunSpec.builder("register${cleanModuleName}Serializers")
                        .receiver(ClassName("com.glureau.k2pb.runtime", "K2PBConfig"))
                        .apply {
                            protobufAggregator.nodes.forEach {
                                val className = it.asClassName()
                                val serializerClassName = it.serializerClassName()
                                when (it) {
                                    is MessageNode -> {
                                        if (it.isPolymorphic) {
                                            addStatement(
                                                "registerSerializer(%T::class, %T())",
                                                className,
                                                serializerClassName
                                            )
                                        } else {
                                            addStatement(
                                                "registerSerializer(%T::class, %T())",
                                                className,
                                                serializerClassName
                                            )
                                        }
                                        it.superTypes.forEach { s ->
                                            addStatement("registerPolymorphicChild(%T::class, %T::class)", s, className)
                                        }
                                    }

                                    is EnumNode -> {
                                        addStatement(
                                            "registerSerializer(%T::class, %T())",
                                            className,
                                            serializerClassName
                                        )
                                    }

                                    is ObjectNode -> {
                                        addStatement(
                                            "registerSerializer(%T::class, %T())",
                                            className,
                                            serializerClassName
                                        )
                                    }
                                }
                            }
                        }
                        .build())
                .build(),
            true
        )
        return fileSpecs + moduleCodeFile
    }
}