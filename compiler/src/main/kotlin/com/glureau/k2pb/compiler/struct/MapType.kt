package com.glureau.k2pb.compiler.struct

data class MapType(val keyType: FieldType, val valueType: FieldType) : FieldType {
    override fun toString(): String = "map<$keyType, $valueType>"
}