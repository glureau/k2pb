package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.appendComment
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FunSpec

data class TypedField(
    override val comment: String?,
    val type: FieldType,
    override val name: String,
    override val protoNumber: Int,
    val annotatedName: String?,
    val annotatedNumber: Int?,
    val annotatedSerializer: KSType? = null,
) : FieldInterface

// TODO: NumberManager should be used at parsing time and not here
fun StringBuilder.appendTypedField(indentLevel: Int, field: TypedField, numberManager: NumberManager) {
    appendComment(indentLevel, field.comment)

    append(indentation(indentLevel))
    appendFieldType(field.type, field.annotatedSerializer)
    append(" ")
    append(field.annotatedName ?: field.name)
    append(" = ")
    append(numberManager.resolve(field.name, field.annotatedNumber))
    appendLine(";")
}

fun FunSpec.Builder.encodeTypedField(field: TypedField) {
    val tag = field.protoNumber
    when (field.type) {
        is ListType -> encodeListType(field.name, field.type, tag)
        is MapType -> encodeMapType(field.name, field.type, tag)
        is ReferenceType -> encodeReferenceType(field.name, field.type, tag, field.annotatedSerializer)
        is ScalarFieldType -> encodeScalarFieldType(field.name, field.type, tag, field.annotatedSerializer)
    }
}

fun FunSpec.Builder.decodeTypedFieldVariableDefinition(field: TypedField) {
    when (field.type) {
        is ListType -> decodeListTypeVariableDefinition(field.name, field.type)
        is MapType -> decodeMapTypeVariableDefinition(field.name, field.type)
        is ReferenceType -> decodeReferenceTypeVariableDefinition(field.name, field.type, field.annotatedSerializer)
        is ScalarFieldType -> decodeScalarTypeVariableDefinition(field.name, field.type, field.annotatedSerializer)
    }
}

fun FunSpec.Builder.decodeTypedField(field: TypedField) {
    when (field.type) {
        is ListType -> decodeListType(field.name, field.type)
        is MapType -> decodeMapType(field.name, field.type)

        is ReferenceType -> decodeReferenceType(field.name, field.type, field.annotatedSerializer)
        is ScalarFieldType -> decodeScalarType(field.name, field.type, field.annotatedSerializer)
    }
}
