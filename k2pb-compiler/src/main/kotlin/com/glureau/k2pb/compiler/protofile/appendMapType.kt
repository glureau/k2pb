package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.MapType
import com.glureau.k2pb.compiler.struct.ScalarType
import com.glureau.k2pb.compiler.struct.useEnumAsKey

fun StringBuilder.appendMapTypeComment(indentLevel: Int, type: MapType) {
    if (type.keyType.useEnumAsKey()) {
        appendComment(indentLevel, "Protobuf does not support enum as map key, using int32 instead.")
    }
}

fun StringBuilder.appendMapType(type: MapType) {
    append("map<")
    if (type.keyType.useEnumAsKey()) {
        append(ScalarType.int32.name)
    } else {
        appendFieldType(type.keyType, null)
    }
    append(", ")
    appendFieldType(type.valueType, null)
    append(">")
}
