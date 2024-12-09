package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.ProtoIntegerType
import com.glureau.k2pb.compiler.poet.ProtoIntegerTypeDefault
import com.glureau.k2pb.compiler.poet.readMessageExt
import com.glureau.k2pb.compiler.poet.writeMessageExt
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName

data class ListType(val repeatedType: FieldType, override val isNullable: Boolean) : FieldType

fun StringBuilder.appendListType(type: ListType, annotatedSerializer: KSType?) {
    append("repeated ")
    appendFieldType(type.repeatedType, annotatedSerializer)
}

fun StringBuilder.appendKotlinListDefinition(type: ListType) = apply {
    append("List<${appendKotlinDefinition(type.repeatedType)}>" + if (type.isNullable) "?" else "")
}

fun FunSpec.Builder.encodeListType(
    instanceName: String,
    fieldName: String,
    listType: ListType,
    tag: Int,
    nullabilitySubField: NullabilitySubField?
) {
    if (nullabilitySubField != null) {
        beginControlFlow("if ($instanceName.$fieldName != null)")
    }

    when (listType.repeatedType) {
        is ScalarFieldType -> {
            // https://protobuf.dev/programming-guides/encoding/#packed
            when (listType.repeatedType.protoType) {
                ScalarType.string, ScalarType.bytes -> {
                    beginControlFlow("$instanceName.$fieldName.forEach /* scalar bs */")
                    addCode(listType.repeatedType.safeWriteMethod("it", tag, null, true))
                    addStatement("")
                    endControlFlow() // forEach
                }
                else -> {
                    beginControlFlow("%M($tag)", writeMessageExt)
                    beginControlFlow("$instanceName.$fieldName.forEach /* scalar */")
                    addCode(listType.repeatedType.safeWriteMethodNoTag("it", null, true))
                    addStatement("")
                    endControlFlow() // forEach
                    endControlFlow() // writeMessage {}
                }
            }
        }

        is ReferenceType -> {
            beginControlFlow("$instanceName.$fieldName.forEach /* ref */")
            encodeReferenceType("it", listType.repeatedType, tag, null, null)
            endControlFlow()
        }

        else -> {
            addStatement("/* ${listType.repeatedType} */")
        }
    }


    if (nullabilitySubField != null) {
        endControlFlow() // if
        beginControlFlow("else")
        addStatement(
            "writeInt(value = 1, tag = ${nullabilitySubField.protoNumber}, format = %T)",
            ProtoIntegerTypeDefault
        )
        endControlFlow() // else
    }
}

fun FunSpec.Builder.decodeListTypeVariableDefinition(
    fieldName: String,
    listType: ListType,
    nullabilitySubField: NullabilitySubField?
) {
    val typeName = StringBuilder().appendKotlinDefinition(listType)
    addStatement("val ${fieldName}: Mutable${typeName.removeSuffix("?")} = mutableListOf()")
    nullabilitySubField?.let {
        addStatement("var ${nullabilitySubField.fieldName}: Boolean = false")
    }
}

fun FunSpec.Builder.decodeListType(fieldName: String, listType: ListType) {
    when (listType.repeatedType) {
        is ScalarFieldType -> {
            // https://protobuf.dev/programming-guides/encoding/#packed
            when (listType.repeatedType.protoType) {
                ScalarType.string, ScalarType.bytes -> {
                    addCode("$fieldName += ")
                    addCode(listType.repeatedType.readMethodNoTag())
                    addStatement("")
                }

                else -> {
                    beginControlFlow("%M()", readMessageExt)
                    beginControlFlow("while (!eof)")
                    addCode("$fieldName += ")
                    addCode(listType.repeatedType.readMethodNoTag())
                    addStatement("")
                    endControlFlow() // while (!eof)
                    endControlFlow() // readMessage {}
                }
            }
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
