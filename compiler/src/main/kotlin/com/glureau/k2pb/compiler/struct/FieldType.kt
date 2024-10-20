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