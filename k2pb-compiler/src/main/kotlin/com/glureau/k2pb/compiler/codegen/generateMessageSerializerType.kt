package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.compiler.poet.generateDataClassSerializerDecode
import com.glureau.k2pb.compiler.poet.generateDataClassSerializerEncode
import com.glureau.k2pb.compiler.poet.generateInlineSerializerDecode
import com.glureau.k2pb.compiler.poet.generateInlineSerializerEncode
import com.glureau.k2pb.compiler.poet.generateObjectSerializerDecode
import com.glureau.k2pb.compiler.poet.generateObjectSerializerEncode
import com.glureau.k2pb.compiler.poet.generatePolymorphicSerializerDecode
import com.glureau.k2pb.compiler.poet.generatePolymorphicSerializerEncode
import com.glureau.k2pb.compiler.struct.MessageNode
import com.squareup.kotlinpoet.FileSpec

private val protoSerializerName = "protoSerializer"

fun FileSpec.Builder.generateMessageSerializerType(messageNode: MessageNode) = generateSerializerType(
    node = messageNode,
    encodeContent = { instanceName ->
        when {
            messageNode.isObject ->
                generateObjectSerializerEncode(messageNode, instanceName, protoSerializerName)

            messageNode.isPolymorphic ->
                generatePolymorphicSerializerEncode(messageNode, instanceName, protoSerializerName)

            messageNode.isInlineClass ->
                generateInlineSerializerEncode(messageNode, instanceName, protoSerializerName)

            else ->
                generateDataClassSerializerEncode(messageNode, instanceName, protoSerializerName)
        }
    },
    decodeContent = { instanceName ->
        when {
            messageNode.isObject ->
                generateObjectSerializerDecode(messageNode, instanceName, protoSerializerName)

            messageNode.isPolymorphic ->
                generatePolymorphicSerializerDecode(messageNode, instanceName, protoSerializerName)

            messageNode.isInlineClass ->
                generateInlineSerializerDecode(messageNode, instanceName, protoSerializerName)

            else ->
                generateDataClassSerializerDecode(messageNode, instanceName, protoSerializerName)
        }
    }
)
