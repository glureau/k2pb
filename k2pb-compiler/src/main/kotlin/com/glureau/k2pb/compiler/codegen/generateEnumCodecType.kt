package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.asClassName
import com.squareup.kotlinpoet.FileSpec

// TODO: WARNING, this is not checking for ProtoNumber annotations yet, using ordinal does limit to contiguous values
fun FileSpec.Builder.generateEnumCodecType(enumNode: EnumNode) = generateCodecType(
    node = enumNode,
    encodeContent = { instanceName: String, protoCodecName: String ->
        addStatement("if ($instanceName == null) return")
        addCode(ScalarFieldType.Int.safeWriteMethodNoTag("$instanceName.ordinal", null, false))
    },
    decodeContent = { instanceName: String, protoCodecName: String ->
        addStatement(
            "return %T.entries.getOrNull(%L)",
            enumNode.asClassName(),
            ScalarFieldType.Int.readMethodNoTag()
        )
    }
)