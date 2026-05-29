package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.asClassName
import com.squareup.kotlinpoet.FileSpec

fun FileSpec.Builder.generateEnumCodecType(enumNode: EnumNode) = generateCodecType(
    node = enumNode,
    encodeContent = { instanceName: String, protoCodecName: String ->
        addStatement("if ($instanceName == null) return")
        val className = enumNode.asClassName()
        beginControlFlow("val protoNumber = when ($instanceName)")
        for (entry in enumNode.entries) {
            addStatement("%T.${entry.kotlinName} -> ${entry.number}", className)
        }
        endControlFlow()
        addCode(ScalarFieldType.Int.safeWriteMethodNoTag("protoNumber", false))
    },
    decodeContent = { instanceName: String, protoCodecName: String ->
        val className = enumNode.asClassName()
        addCode("val protoNumber = ")
        addCode(ScalarFieldType.Int.readMethodNoTag())
        addStatement("")
        beginControlFlow("return when (protoNumber)")
        for (entry in enumNode.entries) {
            addStatement("${entry.number} -> %T.${entry.kotlinName}", className)
        }
        addStatement("else -> null")
        endControlFlow()
    }
)