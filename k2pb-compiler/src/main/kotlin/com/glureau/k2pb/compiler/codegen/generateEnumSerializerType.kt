package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.asClassName
import com.squareup.kotlinpoet.FileSpec

fun FileSpec.Builder.generateEnumSerializerType(enumNode: EnumNode) = generateSerializerType(
    node = enumNode,
    encodeContent = { instanceName ->
        addStatement("if ($instanceName == null) return")
        addCode(ScalarFieldType.Int.safeWriteMethodNoTag("$instanceName.ordinal /* OR */", null, false))
    },
    decodeContent = {
        addStatement(
            "return %T.entries.getOrNull(%L)",
            enumNode.asClassName(),
            ScalarFieldType.Int.readMethodNoTag()
        )
    }
)