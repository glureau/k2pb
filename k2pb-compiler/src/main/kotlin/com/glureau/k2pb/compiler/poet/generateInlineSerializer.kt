package com.glureau.k2pb.compiler.poet

import com.glureau.k2pb.compiler.mapping.customConverterType
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

fun FunSpec.Builder.generateInlineCodecEncode(
    messageNode: MessageNode,
    instanceName: String
): FunSpec.Builder {
    addStatement("if ($instanceName == null) return")
    require(messageNode.fields.size == 1) { "Only one field is allowed in an inline class: $messageNode" }
    val inlinedField = messageNode.fields.first()
    if (inlinedField is TypedField && inlinedField.type is ScalarFieldType) {
        addCode(inlinedField.type.safeWriteMethodNoTag("$instanceName.${inlinedField.name}",  true))
        addStatement("")
    } else if (inlinedField is TypedField && inlinedField.type is ReferenceType) {
        encodeReferenceType(
            "$instanceName.${inlinedField.name}",
            inlinedField.type,
            tag = null,
            inlinedField.annotatedConverter
        )
    }
    return this
}

fun FunSpec.Builder.generateInlineCodecDecode(
    messageNode: MessageNode,
    instanceName: String,
    protoCodecName: String
): FunSpec.Builder {
    val inlinedField = messageNode.fields.first()
    var localVar: String? = null
    if (inlinedField !is TypedField) TODO("Doesn't support inlinedField $inlinedField")

    if (inlinedField.type is ReferenceType && inlinedField.annotatedConverter != null) {
        inlinedField.annotatedConverter.customConverterType()?.let {
            localVar = decodeInLocalVar(inlinedField.name, inlinedField.annotatedConverter, it)
        }
    }

    addCode("return %T(", messageNode.asClassName())

    val readCodeBlock = when (inlinedField.type) {
        is ScalarFieldType -> inlinedField.type.readMethodNoTag()
        is ReferenceType -> localVar?.let { CodeBlock.of(it) }
        else -> TODO("Does not support inlined field of type ${inlinedField.type}")
    }
    addCode("${inlinedField.name} = ")
    if (!inlinedField.type.isNullable) {
        addCode("requireNotNull(")
    }
    if (readCodeBlock != null) {
        addCode(readCodeBlock)
    } else {
        decodeReferenceType("oot", inlinedField.type as ReferenceType, inlinedField.annotatedConverter)
    }
    if (!inlinedField.type.isNullable) {
        addCode(")")
    }
    addCode(")")

    return this
}