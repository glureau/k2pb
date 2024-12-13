package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.compiler.struct.ObjectNode
import com.glureau.k2pb.compiler.struct.asClassName
import com.squareup.kotlinpoet.FileSpec

fun FileSpec.Builder.generateObjectSerializerType(
    node: ObjectNode,
) = generateSerializerType(
    node = node,
    encodeContent = { instanceName: String, protoSerializerName: String ->
        this
    },
    decodeContent = { instanceName: String, protoSerializerName: String ->
        addStatement("return %T", node.asClassName())
        this
    })