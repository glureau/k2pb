package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.ScalarFieldType

fun StringBuilder.appendScalarType(type: ScalarFieldType) {
    append(type.protoType.name)
}

