package com.glureau.k2pb.compiler.struct

import com.squareup.kotlinpoet.FunSpec

sealed interface FieldInterface {
    val comment: String?
    val name: String
    val protoNumber: Int
}

fun StringBuilder.appendField(indentLevel: Int, field: FieldInterface, numberManager: NumberManager) {
    when (field) {
        is TypedField -> appendTypedField(indentLevel, field, numberManager)
        is OneOfField -> appendOneOfField(indentLevel, field, numberManager)
    }
}

fun FunSpec.Builder.encodeField(field: FieldInterface) {
    when (field) {
        is TypedField -> encodeTypedField(field)
        is OneOfField -> encodeOneOfField(field)
    }
}

fun FunSpec.Builder.decodeField(field: FieldInterface) {
    when (field) {
        is TypedField -> decodeTypedField(field)
        is OneOfField -> decodeOneOfField(field)
    }
}

fun FunSpec.Builder.decodeFieldVariableDefinition(field: FieldInterface) {
    when (field) {
        is TypedField -> decodeTypedFieldVariableDefinition(field)
        is OneOfField -> decodeOneOfFieldVariableDefinition(field)
    }
}

