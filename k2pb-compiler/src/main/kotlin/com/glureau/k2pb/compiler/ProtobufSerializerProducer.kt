package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.struct.addEnumNode
import com.glureau.k2pb.compiler.struct.addMessageNode
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
        val fileSpecs = protobufAggregator.messages
            .map {
                val builder = FileSpec.builder(it.serializerClassName())
                builder.addMessageNode(it)
                CodeFile(builder.build(), false)
            } + protobufAggregator.enums
            .map {
                val builder = FileSpec.builder(it.serializerClassName())
                builder.addEnumNode(it)
                CodeFile(builder.build(), false)
            }

        if (fileSpecs.isEmpty()) {
            return emptyList()
        }

        val packages = protobufAggregator.messages.map { it.packageName } +
                protobufAggregator.enums.map { it.packageName }
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
                            protobufAggregator.messages.forEach {
                                val className = it.asClassName()
                                val serializerClassName = it.serializerClassName()
                                if (it.isPolymorphic) {
                                    /*
                                    addStatement(
                                        "registerPolymorphicParent(%T::class)",
                                        it.asClassName(),
                                    )
                                    */
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
                                    addStatement(
                                        "registerPolymorphicChild(%T::class, %T::class)",
                                        s,
                                        className
                                    )
                                }
                            }
                            protobufAggregator.enums.forEach { enum ->
                                addStatement(
                                    "registerSerializer(%T::class, %T())",
                                    enum.asClassName(),
                                    enum.serializerClassName()
                                )
                            }
                        }
                        .build())
                .build(),
            true
        )
        return fileSpecs + moduleCodeFile
    }
}