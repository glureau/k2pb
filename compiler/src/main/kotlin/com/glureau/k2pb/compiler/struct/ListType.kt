package com.glureau.k2pb.compiler.struct

data class ListType(val repeatedType: FieldType) : FieldType

fun StringBuilder.appendListType(type: ListType) {
    append("repeated ")
    appendFieldType(type.repeatedType)
}