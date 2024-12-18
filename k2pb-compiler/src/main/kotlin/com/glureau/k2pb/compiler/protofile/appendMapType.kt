package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.MapType
import com.glureau.k2pb.compiler.struct.ScalarType
import com.glureau.k2pb.compiler.struct.useEnumAsKey

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
