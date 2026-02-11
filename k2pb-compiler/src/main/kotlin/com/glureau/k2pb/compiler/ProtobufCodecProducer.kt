package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.codegen.generateEnumCodecType
import com.glureau.k2pb.compiler.codegen.generateMessageCodecType
import com.glureau.k2pb.compiler.codegen.generateObjectCodecType
import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.ObjectNode
import com.glureau.k2pb.compiler.struct.asClassName
import com.glureau.k2pb.compiler.struct.codecClassName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName

class ProtobufCodecProducer(private val protobufAggregator: ProtobufAggregator) {
    data class CodeFile(
        val fileSpec: FileSpec,
        val aggregating: Boolean,
    )

    fun buildFileSpecs(moduleName: String): List<CodeFile> {
        val fileSpecs = protobufAggregator.nodes
            .map {
                val builder = FileSpec.builder(it.codecClassName())
                when (it) {
                    is MessageNode -> builder.generateMessageCodecType(it)
                    is EnumNode -> builder.generateEnumCodecType(it)
                    is ObjectNode -> builder.generateObjectCodecType(it)
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
            FileSpec.builder(commonPackage, "${cleanModuleName}Codec")
                .addFunction(
                    FunSpec.builder("register${cleanModuleName}Codecs")
                        .receiver(ClassName("com.glureau.k2pb.runtime", "K2PBConfig"))
                        .apply {
                            protobufAggregator.nodes.forEach {
                                val className = it.asClassName()
                                val codecClassName = it.codecClassName()
                                when (it) {
                                    is MessageNode -> {
                                        addRegisterCodecStatement(
                                            className,
                                            codecClassName,
                                            it.protoName,
                                        )
                                        it.superTypes.forEach { superType ->
                                            addStatement(
                                                """|registerPolymorphicChild(
                                                   |parent = %T::class, 
                                                   |child = %T::class,""".trimMargin(),
                                                (superType as? ParameterizedTypeName)?.rawType ?: superType,
                                                className,
                                            )
                                            addStatement(")")
                                        }
                                    }

                                    is EnumNode -> {
                                        addRegisterCodecStatement(
                                            className,
                                            codecClassName,
                                            it.protoName,
                                        )
                                    }

                                    is ObjectNode -> {
                                        addRegisterCodecStatement(
                                            className,
                                            codecClassName,
                                            it.protoName,
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

private fun FunSpec.Builder.addRegisterCodecStatement(
    className: ClassName,
    codecClassName: ClassName,
    protoName: String,
) {
    addStatement(
        """|registerCodec(
           |targetType = %T::class,
           |codec = %T(),
           |protoMessageName = "%L",
           """.trimMargin(),
        className,
        codecClassName,
        protoName,
    )
    addStatement(")")
}