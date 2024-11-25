package com.glureau.k2pb.compiler.struct

import com.google.devtools.ksp.symbol.KSType

data class ListType(val repeatedType: FieldType, override val isNullable: Boolean) : FieldType

fun StringBuilder.appendListType(type: ListType, annotatedSerializer: KSType?) {
    append("repeated ")
    appendFieldType(type.repeatedType, annotatedSerializer)
}

fun StringBuilder.appendKotlinListDefinition(type: ListType) = apply {
    append("List<${appendKotlinDefinition(type.repeatedType)}>" + if (type.isNullable) "?" else "")
}