package com.glureau.k2pb.compiler.poet

import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.decodeField
import com.glureau.k2pb.compiler.struct.encodeField
import com.squareup.kotlinpoet.FunSpec

fun FunSpec.Builder.generatePolymorphicSerializerEncode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
) {
    addStatement("if (instance == null) return")
    messageNode.fields.forEach {
        encodeField(it)
    }
}

fun FunSpec.Builder.generatePolymorphicSerializerDecode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
) {
    messageNode.fields.forEach { f ->
        decodeField(f)
    }
}