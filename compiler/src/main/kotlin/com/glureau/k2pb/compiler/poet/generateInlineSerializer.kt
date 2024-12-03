package com.glureau.k2pb.compiler.poet

import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.asClassName
import com.glureau.k2pb.compiler.struct.decodeInLocalVar
import com.glureau.k2pb.compiler.struct.decodeReferenceType
import com.glureau.k2pb.compiler.struct.encodeReferenceType
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec

fun FunSpec.Builder.generateInlineSerializerEncode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
) {
    addStatement("if ($instanceName == null) return")

    val inlinedField = messageNode.fields.first()
    if (inlinedField is TypedField && inlinedField.type is ScalarFieldType) {
        addCode(inlinedField.type.safeWriteMethodNoTag("$instanceName.${inlinedField.name} /* M */", null))
        addStatement("")
    } else if (inlinedField is TypedField && inlinedField.type is ReferenceType) {
        encodeReferenceType(
            "$instanceName.${inlinedField.name}",
            inlinedField.type,
            tag = null,
            inlinedField.annotatedSerializer,
            inlinedField.nullabilitySubField
        )
    }
}

fun FunSpec.Builder.generateInlineSerializerDecode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
) {
    val inlinedField = messageNode.fields.first()
    var localVar: String? = null
    if (inlinedField !is TypedField) TODO()

    if (inlinedField.type is ReferenceType && inlinedField.annotatedSerializer != null) {
        localVar = decodeInLocalVar(inlinedField.name, inlinedField.annotatedSerializer)
    }

    addCode("return %T(", messageNode.asClassName())

    val readCodeBlock = when (inlinedField.type) {
        is ScalarFieldType -> inlinedField.type.readMethodNoTag()
        is ReferenceType -> localVar?.let { CodeBlock.of(it) }
        else -> TODO()
    }
    addCode("${inlinedField.name} = ")
    if (!inlinedField.type.isNullable) {
        addCode("requireNotNull(")
    }
    if (readCodeBlock != null) {
        addCode(readCodeBlock)
    } else {
        decodeReferenceType("oot", inlinedField.type as ReferenceType, inlinedField.annotatedSerializer)
    }
    if (!inlinedField.type.isNullable) {
        addCode(")")
    }
    /*

    if (inlinedField.type is ScalarFieldType) {
        if (inlinedField.type.isNullable) {
            addCode("${inlinedField.name} = ${inlinedField.type.readMethodNoTag()} /* U1 */")
        } else {
            addCode("${inlinedField.name} = requireNotNull(${inlinedField.type.readMethodNoTag()}) /* U2 */")
        }
    } else if (inlinedField.type is ReferenceType) {
        addCode("${inlinedField.name} = ${localVar ?: "ooo"} /* U */")
    }
     */
    addCode(")")

}