package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.asClassName
import com.squareup.kotlinpoet.FileSpec

// TODO: WARNING, this is not checking for ProtoNumber annotations yet, using ordinal does limit to contiguous values
fun FileSpec.Builder.generateEnumSerializerType(enumNode: EnumNode) = generateSerializerType(
    node = enumNode,
    encodeContent = { instanceName: String, protoSerializerName: String ->
        addStatement("if ($instanceName == null) return")
        addCode(ScalarFieldType.Int.safeWriteMethodNoTag("$instanceName.ordinal", null, false))
    },
    decodeContent = { instanceName: String, protoSerializerName: String ->
        addStatement(
            "return %T.entries.getOrNull(%L)",
            enumNode.asClassName(),
            ScalarFieldType.Int.readMethodNoTag()
        )
    }
)