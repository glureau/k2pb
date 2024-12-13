package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.compiler.poet.generateDataClassSerializerDecode
import com.glureau.k2pb.compiler.poet.generateDataClassSerializerEncode
import com.glureau.k2pb.compiler.poet.generateInlineSerializerDecode
import com.glureau.k2pb.compiler.poet.generateInlineSerializerEncode
import com.glureau.k2pb.compiler.poet.generatePolymorphicSerializerDecode
import com.glureau.k2pb.compiler.poet.generatePolymorphicSerializerEncode
import com.glureau.k2pb.compiler.struct.MessageNode
import com.squareup.kotlinpoet.FileSpec

fun FileSpec.Builder.generateMessageSerializerType(messageNode: MessageNode) = generateSerializerType(
    node = messageNode,
    encodeContent = { instanceName: String, protoSerializerName: String ->
        when {
            messageNode.isPolymorphic ->
                generatePolymorphicSerializerEncode(messageNode, instanceName, protoSerializerName)

            messageNode.isInlineClass ->
                generateInlineSerializerEncode(messageNode, instanceName, protoSerializerName)

            else ->
                generateDataClassSerializerEncode(messageNode, instanceName, protoSerializerName)
        }
    },
    decodeContent = { instanceName: String, protoSerializerName: String ->
        when {
            messageNode.isPolymorphic ->
                generatePolymorphicSerializerDecode(messageNode, instanceName, protoSerializerName)

            messageNode.isInlineClass ->
                generateInlineSerializerDecode(messageNode, instanceName, protoSerializerName)

            else ->
                generateDataClassSerializerDecode(messageNode, instanceName, protoSerializerName)
        }
    }
)
