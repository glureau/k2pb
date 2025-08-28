package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.annotation.NullableMigration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FunSpec

data class NullabilitySubField(
    val fieldName: String,
    val protoNumber: Int,
    val nullableMigration: NullableMigration,
)

data class TypedField(
    override val comment: String?,
    val type: FieldType,
    override val name: String,
    override val protoNumber: Int,
    private val annotatedName: String?,
    val annotatedConverter: KSType? = null,
    val annotatedNullabilityMigration: NullableMigration?,
    val nullabilitySubField: NullabilitySubField?,
) : FieldInterface {
    val resolvedName = annotatedName ?: name
}

fun FunSpec.Builder.encodeTypedField(instanceName: String, field: TypedField) {
    val tag = field.protoNumber
    when (field.type) {
        is ListType -> encodeListType(instanceName, field.name, field.type, tag)
        is MapType -> encodeMapType(instanceName, field.name, field.type, tag)
        is ReferenceType -> encodeReferenceType(
            "$instanceName.${field.name}",
            field.type,
            tag,
            field.annotatedConverter
        )

        is ScalarFieldType -> encodeScalarFieldType(
            instanceName,
            field.name,
            field.type,
            tag,
            field.annotatedConverter,
            field.nullabilitySubField
        )
    }
}

fun FunSpec.Builder.decodeTypedFieldVariableDefinition(field: TypedField) {
    when (field.type) {
        is ListType -> decodeListTypeVariableDefinition(field.name, field.type, field.nullabilitySubField)
        is MapType -> decodeMapTypeVariableDefinition(field.name, field.type)
        is ReferenceType -> decodeReferenceTypeVariableDefinition(
            field.name,
            field.type,
            field.nullabilitySubField
        )

        is ScalarFieldType -> decodeScalarTypeVariableDefinition(
            field.name,
            field.type,
            field.annotatedConverter,
            field.nullabilitySubField
        )
    }
}

fun FunSpec.Builder.decodeTypedField(field: TypedField) {
    when (field.type) {
        is ListType -> decodeListType(field.name, field.type)
        is MapType -> decodeMapType(field.name, field.type)

        is ReferenceType -> {
            decodeReferenceType(field.name, field.type, field.annotatedConverter)
            beginControlFlow("?.let")
            addStatement("${field.name} = it")
            endControlFlow()
        }

        is ScalarFieldType -> decodeScalarType(field.name, field.type, field.annotatedConverter)
    }
}
