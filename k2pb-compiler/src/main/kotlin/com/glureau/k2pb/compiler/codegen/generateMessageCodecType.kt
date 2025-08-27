package com.glureau.k2pb.compiler.codegen

import com.glureau.k2pb.compiler.poet.generateDataClassCodecDecode
import com.glureau.k2pb.compiler.poet.generateDataClassCodecEncode
import com.glureau.k2pb.compiler.poet.generateInlineCodecDecode
import com.glureau.k2pb.compiler.poet.generateInlineCodecEncode
import com.glureau.k2pb.compiler.poet.generatePolymorphicCodecDecode
import com.glureau.k2pb.compiler.poet.generatePolymorphicCodecEncode
import com.glureau.k2pb.compiler.struct.MessageNode
import com.squareup.kotlinpoet.FileSpec

fun FileSpec.Builder.generateMessageCodecType(messageNode: MessageNode) = generateCodecType(
    node = messageNode,
    encodeContent = { instanceName: String, protoCodecName: String ->
        when {
            messageNode.isPolymorphic ->
                generatePolymorphicCodecEncode(messageNode, instanceName, protoCodecName)

            messageNode.isInlineClass ->
                generateInlineCodecEncode(messageNode, instanceName, protoCodecName)

            else ->
                generateDataClassCodecEncode(messageNode, instanceName)
        }
    },
    decodeContent = { instanceName: String, protoCodecName: String ->
        when {
            messageNode.isPolymorphic ->
                generatePolymorphicCodecDecode(messageNode, instanceName, protoCodecName)

            messageNode.isInlineClass ->
                generateInlineCodecDecode(messageNode, instanceName, protoCodecName)

            else ->
                generateDataClassCodecDecode(messageNode, instanceName, protoCodecName)
        }
    }
)
