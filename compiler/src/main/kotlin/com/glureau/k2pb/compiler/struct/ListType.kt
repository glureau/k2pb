package com.glureau.k2pb.compiler.struct

data class ListType(val repeatedType: FieldType) : FieldType {
    override fun toString(): String {
        return "repeated $repeatedType"
    }
}