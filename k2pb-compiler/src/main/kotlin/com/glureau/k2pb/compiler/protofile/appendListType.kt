package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.ListType
import com.google.devtools.ksp.symbol.KSType

fun StringBuilder.appendListType(type: ListType, annotatedSerializer: KSType?) {
    append("repeated ")
    appendFieldType(type.repeatedType, annotatedSerializer)
}