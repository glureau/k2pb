package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.MapType

fun StringBuilder.appendMapType(type: MapType) {
    append("map<")
    appendFieldType(type.keyType, null)
    append(", ")
    appendFieldType(type.valueType, null)
    append(">")
}
