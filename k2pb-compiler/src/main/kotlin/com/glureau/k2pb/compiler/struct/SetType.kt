package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.poet.readMessageExt
import com.glureau.k2pb.compiler.poet.writeMessageExt
import com.squareup.kotlinpoet.CodeBlock
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
                    beginControlFlow("if ($instanceName.$fieldName.isNotEmpty())")
                    beginControlFlow("%M($tag)", writeMessageExt)
                    beginControlFlow("$instanceName.$fieldName.forEach")
                    addCode(setType.repeatedType.safeWriteMethodNoTag("it", true))
                    addStatement("")
                    endControlFlow() // forEach
                    endControlFlow() // writeMessage {}
                    endControlFlow() // isNotEmpty
                }
            }
        }

        is ReferenceType -> {
            if (setType.repeatedType.isEnum) {
                // Enums use packed encoding like scalars in proto3
                beginControlFlow("if ($instanceName.$fieldName.isNotEmpty())")
                beginControlFlow("%M($tag)", writeMessageExt)
                beginControlFlow("$instanceName.$fieldName.forEach")
                beginControlFlow("with(protoCodec)")
                addStatement("encode(it, %T::class)", setType.repeatedType.className.copy(nullable = false))
                endControlFlow() // with
                endControlFlow() // forEach
                endControlFlow() // writeMessage {}
                endControlFlow() // isNotEmpty
            } else {
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
        }

        else -> {
            Logger.error("Nested collection detected in Set encode ($fieldName): ${setType.repeatedType} is not supported and data would be silently lost.")
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
                    beginControlFlow("%M", readMessageExt)
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
            if (setType.repeatedType.isEnum) {
                // Enums use packed encoding like scalars in proto3
                beginControlFlow("%M", readMessageExt)
                beginControlFlow("while (!eof)")
                beginControlFlow("with(protoCodec)")
                addStatement("decode(%T::class)?.let { $fieldName += it }", setType.repeatedType.className.copy(nullable = false))
                endControlFlow() // with
                endControlFlow() // while (!eof)
                endControlFlow() // readMessage {}
            } else {
                decodeReferenceType(fieldName, setType.repeatedType, null)
                beginControlFlow("?.let")
                addStatement("$fieldName += it")
                endControlFlow()
            }
        }

        else -> {
            Logger.error("Nested collection detected in Set decode ($fieldName): ${setType.repeatedType} is not supported and data would be silently lost.")
        }
    }
}
