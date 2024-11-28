package com.glureau.k2pb.compiler.struct

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FunSpec

data class ListType(val repeatedType: FieldType, override val isNullable: Boolean) : FieldType

fun StringBuilder.appendListType(type: ListType, annotatedSerializer: KSType?) {
    append("repeated ")
    appendFieldType(type.repeatedType, annotatedSerializer)
}

fun StringBuilder.appendKotlinListDefinition(type: ListType) = apply {
    append("List<${appendKotlinDefinition(type.repeatedType)}>" + if (type.isNullable) "?" else "")
}

fun FunSpec.Builder.encodeListType(fieldName: String, listType: ListType, tag: Int) {
    beginControlFlow("%M($tag)", writeMessageExt)
    beginControlFlow("instance.$fieldName.forEach")
    when (listType.repeatedType) {
        is ScalarFieldType -> addCode(listType.repeatedType.writeMethodNoTag("it"))
            .also { addStatement("") }

        else -> TODO("Not supported yet")
    }
    endControlFlow()
    endControlFlow()
}

fun FunSpec.Builder.decodeListTypeVariableDefinition(fieldName: String, listType: ListType) {
    val typeName = StringBuilder().appendKotlinDefinition(listType)
    addStatement("val ${fieldName}: Mutable${typeName} = mutableListOf()")
}

fun FunSpec.Builder.decodeListType(fieldName: String, listType: ListType) {
    beginControlFlow("%M()", readMessageExt)
    when (listType.repeatedType) {
        is ScalarFieldType -> {
            beginControlFlow("while (!eof)")
            addCode("$fieldName += ")
            addCode(listType.repeatedType.readMethodNoTag())
            addStatement("")
            endControlFlow()
        }

        else -> TODO("Not supported yet")
    }
    endControlFlow()
}
