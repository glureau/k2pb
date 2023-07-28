package com.glureau.k2pb.compiler.struct

data class ReferenceType(val name: String) : FieldType {
    override fun toString() = name
}