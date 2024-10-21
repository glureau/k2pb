package com.glureau.k2pb.compiler.struct

sealed interface FieldType

fun StringBuilder.appendFieldType(type: FieldType) {
    when (type) {
        is ScalarFieldType -> appendScalarType(type)
        is ReferenceType -> appendReferenceType(type)
        is ListType -> appendListType(type)
        is MapType -> appendMapType(type)
    }
}

fun StringBuilder.appendKotlinDefinition(type: FieldType): String {
    return when (type) {
        is ScalarFieldType -> type.kotlinClass.canonicalName
        is ReferenceType -> type.name
        is ListType -> appendKotlinListDefinition(type).toString()
        is MapType -> appendKotlinMapDefinition(type).toString()
    }
}