package com.glureau.k2pb.compiler.poet

import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.asClassName
import com.squareup.kotlinpoet.FunSpec

fun FunSpec.Builder.generateObjectSerializerEncode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
): FunSpec.Builder {
    // Nothing to do, dummy class for simple implementation
    return this
}

fun FunSpec.Builder.generateObjectSerializerDecode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
): FunSpec.Builder {
    addStatement("return %T", messageNode.asClassName())
    return this
}