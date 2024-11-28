package com.glureau.k2pb.compiler.poet

import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.asClassName
import com.glureau.k2pb.compiler.struct.decodeReferenceType
import com.glureau.k2pb.compiler.struct.encodeReferenceType
import com.squareup.kotlinpoet.FunSpec

fun FunSpec.Builder.generateInlineSerializerEncode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
) {
    addStatement("if (instance == null) return")

    val inlinedField = messageNode.fields.first()
    if (inlinedField is TypedField && inlinedField.type is ScalarFieldType) {
        addCode(inlinedField.type.writeMethodNoTag("instance.${inlinedField.name} /* M */"))
        addStatement("")
    } else if (inlinedField is TypedField && inlinedField.type is ReferenceType) {
        encodeReferenceType(inlinedField.name, inlinedField.type, tag = null, inlinedField.annotatedSerializer)
    }
}

fun FunSpec.Builder.generateInlineSerializerDecode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
) {
    addCode("return %T(", messageNode.asClassName())
    val inlinedField = messageNode.fields.first()
    if (inlinedField is TypedField && inlinedField.type is ScalarFieldType) {
        addCode("${inlinedField.name} = ${inlinedField.type.readMethodNoTag()} /* U */")
    } else if (inlinedField is TypedField && inlinedField.type is ReferenceType) {
        decodeReferenceType(inlinedField.name, inlinedField.type, inlinedField.annotatedSerializer)
    }
    addCode(")")

}