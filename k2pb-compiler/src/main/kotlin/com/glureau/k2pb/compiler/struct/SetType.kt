package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.poet.readMessageExt
import com.glureau.k2pb.compiler.poet.writeMessageExt
import com.squareup.kotlinpoet.FunSpec

data class SetType(val repeatedType: FieldType, override val isNullable: Boolean) : FieldType

fun StringBuilder.appendKotlinSetDefinition(type: SetType) = apply {
    append("Set<${appendKotlinDefinition(type.repeatedType)}>" + if (type.isNullable) "?" else "")
}

fun FunSpec.Builder.encodeSetType(
    instanceName: String,
    fieldName: String,
    setType: SetType,
    tag: Int,
) {
    if (setType.isNullable) {
        beginControlFlow("if ($instanceName.$fieldName != null)")
    }

    when (setType.repeatedType) {
        is ScalarFieldType -> {
            // https://protobuf.dev/programming-guides/encoding/#packed
            when (setType.repeatedType.protoType) {
                ScalarType.string, ScalarType.bytes -> {
                    beginControlFlow("$instanceName.$fieldName.forEach")
                    addCode(setType.repeatedType.safeWriteMethod("it", tag, true))
                    addStatement("")
                    endControlFlow() // forEach
                }

                else -> {
                    beginControlFlow("%M($tag)", writeMessageExt)
                    beginControlFlow("$instanceName.$fieldName.forEach")
                    addCode(setType.repeatedType.safeWriteMethodNoTag("it", true))
                    addStatement("")
                    endControlFlow() // forEach
                    endControlFlow() // writeMessage {}
                }
            }
        }

        is ReferenceType -> {
            beginControlFlow("$instanceName.$fieldName.forEach")
            encodeReferenceType(
                fieldName = "it",
                type = setType.repeatedType,
                tag = tag,
                annotatedCodec = null,
                forceEncodeDefault = true,
            )
            endControlFlow()
        }

        else -> {
            addStatement("/* ${setType.repeatedType} */")
        }
    }

    if (setType.isNullable) {
        endControlFlow() // if
    }
}

fun FunSpec.Builder.decodeSetTypeVariableDefinition(
    fieldName: String,
    setType: SetType,
    nullabilitySubField: NullabilitySubField?,
) {
    val typeName = StringBuilder().appendKotlinDefinition(setType)
    addStatement("val ${fieldName}: Mutable${typeName.removeSuffix("?")} = mutableSetOf()")
    nullabilitySubField?.let {
        addNullabilityStatement(nullabilitySubField)
    }
}

fun FunSpec.Builder.decodeSetType(fieldName: String, setType: SetType) {
    when (setType.repeatedType) {
        is ScalarFieldType -> {
            // https://protobuf.dev/programming-guides/encoding/#packed
            when (setType.repeatedType.protoType) {
                ScalarType.string, ScalarType.bytes -> {
                    addCode("$fieldName += ")
                    addCode(setType.repeatedType.readMethodNoTag())
                    addStatement("")
                }

                else -> {
                    beginControlFlow("%M()", readMessageExt)
                    beginControlFlow("while (!eof)")
                    addCode("$fieldName += ")
                    addCode(setType.repeatedType.readMethodNoTag())
                    addStatement("")
                    endControlFlow() // while (!eof)
                    endControlFlow() // readMessage {}
                }
            }
        }

        is ReferenceType -> {
            decodeReferenceType(fieldName, setType.repeatedType, null)
            beginControlFlow("?.let")
            addStatement("$fieldName += it")
            endControlFlow()
        }

        else -> {
            addStatement("/* ${setType.repeatedType} */")
            //TODO("Not supported yet")
        }
    }
}
