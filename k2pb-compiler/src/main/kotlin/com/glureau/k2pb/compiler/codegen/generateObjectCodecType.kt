package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.compiler.struct.ObjectNode
import com.glureau.k2pb.compiler.struct.asClassName
import com.squareup.kotlinpoet.FileSpec

fun FileSpec.Builder.generateObjectCodecType(
    node: ObjectNode,
) = generateCodecType(
    node = node,
    encodeContent = { instanceName: String, protoCodecName: String ->
        this
    },
    decodeContent = { instanceName: String, protoCodecName: String ->
        addStatement("return %T", node.asClassName())
        this
    })