package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.appendComment
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toClassName

data class TypedField(
    override val comment: String?,
    val type: FieldType,
    override val name: String,
    override val protoNumber: Int,
    val annotatedName: String?,
    val annotatedNumber: Int?,
    val annotatedSerializer: KSType? = null,
) : FieldInterface

// TODO: NumberManager should be used at parsing time and not here
fun StringBuilder.appendTypedField(indentLevel: Int, field: TypedField, numberManager: NumberManager) {
    appendComment(indentLevel, field.comment)

    append(indentation(indentLevel))
    appendFieldType(field.type, field.annotatedSerializer)
    append(" ")
    append(field.annotatedName ?: field.name)
    append(" = ")
    append(numberManager.resolve(field.name, field.annotatedNumber))
    appendLine(";")
}

fun FunSpec.Builder.encodeTypedField(field: TypedField) {
    val tag = field.protoNumber
    when (field.type) {
        is ListType -> {
            beginControlFlow("%M($tag)", writeMessageExt)
            beginControlFlow("instance.${field.name}.forEach")
            when (field.type.repeatedType) {
                is ScalarFieldType -> addCode(field.type.repeatedType.writeMethodNoTag("it"))
                    .also { addStatement("") }

                else -> TODO("Not supported yet")
            }
            endControlFlow()
            endControlFlow()
        }

        is MapType -> {
            beginControlFlow("instance.${field.name}.forEach")
            beginControlFlow("%M($tag)", writeMessageExt)
            when (field.type.keyType) {
                is ScalarFieldType -> addCode(field.type.keyType.writeMethod("it.key", 1))
                    .also { addStatement("") }

                else -> TODO("Not supported yet")
            }
            when (field.type.valueType) {
                is ScalarFieldType -> addCode(field.type.valueType.writeMethod("it.value", 2))
                    .also { addStatement("") }

                else -> TODO("Not supported yet")
            }
            endControlFlow() // writeMessage of the item
            endControlFlow() // forEach
        }

        is ReferenceType -> {
            field.type.inlineOf?.let { inlineOf ->
                require(inlineOf is ScalarFieldType) // Not supporting other types for now...
                encodeTypedField(
                    TypedField(
                        comment = field.comment,
                        type = inlineOf,
                        name = field.name + "." + field.type.inlineName,
                        protoNumber = tag,
                        annotatedName = field.annotatedName,
                        annotatedNumber = field.annotatedNumber,
                        annotatedSerializer = field.type.inlineAnnotatedSerializer ?: field.annotatedSerializer,
                    )
                )
            } ?: run {
                beginControlFlow("%M($tag) {", writeMessageExt)
                beginControlFlow("with(delegate) {")
                addStatement("encode(instance.${field.name}, ${field.type.name}::class)")
                endControlFlow()
                endControlFlow()
            }
        }

        is ScalarFieldType -> {
            (field.annotatedSerializer?.let { s ->
                val encodedTmpName = "${field.name.replace(".", "_")}Encoded"
                addStatement(
                    "val $encodedTmpName = %T().encode(instance.${field.name})",
                    s.toClassName()
                )
                addCode(field.type.writeMethod(encodedTmpName, tag))
            } ?: addCode(field.type.writeMethod("instance.${field.name}", tag)))
                .also { addStatement("") }
        }
    }
}

fun FunSpec.Builder.decodeTypedFieldVariableDefinition(field: TypedField) {
    when (field.type) {
        is ListType -> {
            val typeName = StringBuilder().appendKotlinDefinition(field.type)
            addStatement("val ${field.name}: Mutable${typeName} = mutableListOf()")
        }

        is MapType -> {
            val typeName = StringBuilder().appendKotlinDefinition(field.type)
            addStatement("val ${field.name}: Mutable${typeName} = mutableMapOf()")
        }

        is ReferenceType,
        is ScalarFieldType -> {
            val typeName = StringBuilder().appendKotlinDefinition(field.type)
            addStatement("var ${field.name}: ${typeName}? = null")
        }
    }
}

fun FunSpec.Builder.decodeTypedField(field: TypedField) {
    when (field.type) {
        is ListType -> {
            beginControlFlow("%M()", readMessageExt)
            when (field.type.repeatedType) {
                is ScalarFieldType -> {
                    beginControlFlow("while (!eof)")
                    addCode("${field.name} += ")
                    addCode(field.type.repeatedType.readMethodNoTag())
                    addStatement("")
                    endControlFlow()
                }

                else -> TODO("Not supported yet")
            }
            endControlFlow()
        }

        is MapType -> {
            beginControlFlow("%M()", readMessageExt)
            addStatement("readTag()")
            addCode("val key = ")
            when (field.type.keyType) {
                is ScalarFieldType -> addCode(field.type.keyType.readMethodNoTag())
                else -> TODO()
            }
            addStatement("")

            addStatement("readTag()")
            addCode("val value = ")
            when (field.type.valueType) {
                is ScalarFieldType -> addCode(field.type.valueType.readMethodNoTag())
                else -> TODO()
            }
            addStatement("")
            addStatement("${field.name}[key] = value")
            endControlFlow()
        }

        is ReferenceType -> {
            field.type.inlineOf?.let { inlineOf ->
                require(inlineOf is ScalarFieldType) // Not supporting other types for now...

                (field.type.inlineAnnotatedSerializer ?: field.annotatedSerializer)?.let { s ->
                    val encodedTmpName = "${field.name.replace(".", "_")}Encoded"
                    addStatement(
                        "val $encodedTmpName = %T().decode(${inlineOf.readMethod()})",
                        s.toClassName()
                    )
                    addStatement("${field.name} = ${field.type.name}($encodedTmpName)")
                } ?: addStatement("${field.name} = ${field.type.name}(${inlineOf.readMethod()})")

            }
                ?: run {
                    beginControlFlow("${field.name} = %M", readMessageExt)
                    beginControlFlow("with(delegate) {")
                    addStatement("decode(${field.type.name}::class)")
                    endControlFlow()
                    endControlFlow()
                }
        }

        is ScalarFieldType -> {
            addStatement("${field.name} = ${field.type.readMethod()}")
        }
    }
}