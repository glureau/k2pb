package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.appendComment
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FunSpec

data class NullabilitySubField(
    val fieldName: String,
    val protoNumber: Int,
)

data class TypedField(
    override val comment: String?,
    val type: FieldType,
    override val name: String,
    override val protoNumber: Int,
    private val annotatedName: String?,
    val annotatedSerializer: KSType? = null,
    val nullabilitySubField: NullabilitySubField?,
) : FieldInterface {
    val resolvedName = annotatedName ?: name
}

fun StringBuilder.appendTypedField(indentLevel: Int, field: TypedField) {
    appendComment(indentLevel, field.comment)

    append(indentation(indentLevel))
    appendFieldType(field.type, field.annotatedSerializer)
    append(" ")
    append(field.resolvedName)
    append(" = ")
    append(field.protoNumber)
    appendLine(";")
    if (field.nullabilitySubField != null && field.annotatedSerializer == null) {
        append(indentation(indentLevel))
        appendFieldType(ScalarFieldType.Boolean, null)
        append(" ")
        append(field.nullabilitySubField.fieldName)
        append(" = ")
        append(field.nullabilitySubField.protoNumber)
        appendLine(";")
    }
}

fun FunSpec.Builder.encodeTypedField(instanceName: String, field: TypedField) {
    val tag = field.protoNumber
    when (field.type) {
        is ListType -> encodeListType(instanceName, field.name, field.type, tag)
        is MapType -> encodeMapType(field.name, field.type, tag)
        is ReferenceType -> encodeReferenceType(
            "$instanceName.${field.name}",
            field.type,
            tag,
            field.annotatedSerializer,
            field.nullabilitySubField
        )

        is ScalarFieldType -> encodeScalarFieldType(
            field.name,
            field.type,
            tag,
            field.annotatedSerializer,
            field.nullabilitySubField
        )
    }
}

fun FunSpec.Builder.decodeTypedFieldVariableDefinition(field: TypedField) {
    when (field.type) {
        is ListType -> decodeListTypeVariableDefinition(field.name, field.type)
        is MapType -> decodeMapTypeVariableDefinition(field.name, field.type)
        is ReferenceType -> decodeReferenceTypeVariableDefinition(
            field.name,
            field.type,
            field.annotatedSerializer,
            field.nullabilitySubField
        )

        is ScalarFieldType -> decodeScalarTypeVariableDefinition(
            field.name,
            field.type,
            field.annotatedSerializer,
            field.nullabilitySubField
        )
    }
}

fun FunSpec.Builder.decodeTypedField(field: TypedField) {
    when (field.type) {
        is ListType -> decodeListType(field.name, field.type)
        is MapType -> decodeMapType(field.name, field.type)

        is ReferenceType -> {
            decodeReferenceType(field.name, field.type, field.annotatedSerializer)
            beginControlFlow("?.let")
            addStatement("${field.name} = it")
            endControlFlow()
        }

        is ScalarFieldType -> decodeScalarType(field.name, field.type, field.annotatedSerializer)
    }
}
