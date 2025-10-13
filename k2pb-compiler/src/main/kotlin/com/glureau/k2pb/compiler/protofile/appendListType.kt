package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.FieldType
import com.glureau.k2pb.compiler.struct.ListType
import com.google.devtools.ksp.symbol.KSType

fun StringBuilder.appendListType(repeatedType: FieldType, annotatedCodec: KSType?) {
    append("repeated ")
    appendFieldType(repeatedType, annotatedCodec)
}