package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.struct.addMessageNote
import com.glureau.k2pb.compiler.struct.asClassName
import com.glureau.k2pb.compiler.struct.serializerClassName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.util.Locale

class ProtobufSerializerProducer(private val protobufAggregator: ProtobufAggregator) {
    data class CodeFile(
        val fileSpec: FileSpec,
        val aggregating: Boolean,
    )

    fun buildFileSpecs(moduleName: String): List<CodeFile> {
        val fileSpecs = protobufAggregator.messages.map {
            val builder = FileSpec.builder(it.packageName, "${it.name}Serializer")
            builder.addMessageNote(it)
            CodeFile(builder.build(), false)
        }
        val packages = protobufAggregator.messages.map { it.packageName }
        // Find the common package
        val commonPackage =
            //if (packages.isEmpty()) ""
             packages.reduce { acc, s -> acc.commonPrefixWith(s) }

        // sample-lib => SampleLib
        val cleanModuleName = moduleName
            .split("-")
            .joinToString("") { it.capitalize(Locale.US) }

        val moduleCodeFile = CodeFile(
            FileSpec.builder(commonPackage, "${cleanModuleName}Serializers")
                .addFunction(FunSpec.builder("register${cleanModuleName}Serializers")
                    .receiver(ClassName("com.glureau.k2pb.runtime", "K2PBConfig"))
                    .apply {
                        protobufAggregator.messages.forEach {
                            addStatement(
                                "registerSerializer(%T::class, %T())",
                                it.asClassName(),
                                it.serializerClassName()
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