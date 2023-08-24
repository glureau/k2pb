package com.glureau.k2pb.compiler.struct

data class MapType(val keyType: FieldType, val valueType: FieldType) : FieldType

fun StringBuilder.appendMapType(type: MapType) {
    append("map<")
    appendFieldType(type.keyType)
    append(", ")
    appendFieldType(type.valueType)
    append(">")
}