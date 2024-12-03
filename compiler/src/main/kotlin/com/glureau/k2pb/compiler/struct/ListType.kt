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

fun FunSpec.Builder.encodeListType(instanceName: String, fieldName: String, listType: ListType, tag: Int) {
    when (listType.repeatedType) {
        is ScalarFieldType -> {
            beginControlFlow("%M($tag)", writeMessageExt)
            beginControlFlow("$instanceName.$fieldName.forEach")
            addCode(listType.repeatedType.safeWriteMethodNoTag("it", null))
            addStatement("")
            endControlFlow() // forEach
            endControlFlow() // writeMessage {}
        }

        is ReferenceType -> {
            beginControlFlow("$instanceName.$fieldName.forEach")
            encodeReferenceType("it", listType.repeatedType, tag, null, null)
            endControlFlow()
        }

        else -> {
            addStatement("/* ${listType.repeatedType} */")
        }
    }
}

fun FunSpec.Builder.decodeListTypeVariableDefinition(fieldName: String, listType: ListType) {
    val typeName = StringBuilder().appendKotlinDefinition(listType)
    addStatement("val ${fieldName}: Mutable${typeName} = mutableListOf()")
}

fun FunSpec.Builder.decodeListType(fieldName: String, listType: ListType) {
    when (listType.repeatedType) {
        is ScalarFieldType -> {
            beginControlFlow("%M()", readMessageExt)
            beginControlFlow("while (!eof)")
            addCode("$fieldName += ")
            addCode(listType.repeatedType.readMethodNoTag())
            addStatement("")
            endControlFlow() // while (!eof)
            endControlFlow() // readMessage {}
        }

        is ReferenceType -> {
            decodeReferenceType(fieldName, listType.repeatedType, null)
            beginControlFlow("?.let")
            addStatement("$fieldName += it")
            endControlFlow()
        }

        else -> {
            addStatement("/* ${listType.repeatedType} */")
            //TODO("Not supported yet")
        }
    }
}
