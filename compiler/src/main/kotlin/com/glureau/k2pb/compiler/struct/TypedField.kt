package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.CustomStringConverter
import com.glureau.k2pb.compiler.mapping.appendComment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName
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
            (field.annotatedSerializer ?: field.type.inlineAnnotatedSerializer)?.let { annotatedSerializer ->
                val fieldAccess = "instance.${field.name}" + (field.type.inlineName?.let { ".$it" } ?: "")

                if (field.type.isNullable|| (field.type.inlineOf as? ReferenceType)?.isNullable == true ) {
                    beginControlFlow("if ($fieldAccess != null)")
                }
                val encodedTmpName = "${field.name.replace(".", "_")}Encoded"
                addStatement("val $encodedTmpName = %T().encode($fieldAccess)", annotatedSerializer.toClassName())
                val parents = (annotatedSerializer.declaration as KSClassDeclaration)
                    .superTypes
                    .map { it.resolve().toClassName() }
                if (parents.contains(CustomStringConverter::class.asClassName())) {
                    addCode(ScalarFieldType.String.writeMethod(encodedTmpName, tag))
                    addStatement("")
                } else {
                    error("Not supported yet")
                }
                if (field.type.isNullable) {
                    endControlFlow()
                }
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
                addStatement(" // scalar")
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
            field.annotatedSerializer?.let { type ->
                val parents = (type.declaration as KSClassDeclaration)
                    .superTypes
                    .map { it.resolve().toClassName() }
                if (parents.contains(CustomStringConverter::class.asClassName())) {
                    addStatement("var ${field.name}: String? = null")
                } else {
                    error("Not supported yet")
                }
            } ?: run {
                addStatement("var ${field.name}: ${typeName}? = null")
            }

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
            (field.annotatedSerializer ?: field.type.inlineAnnotatedSerializer)?.let { annotatedSerializer ->
                val parents = (annotatedSerializer.declaration as KSClassDeclaration)
                    .superTypes
                    .map { it.resolve().toClassName() }
                if (parents.contains(CustomStringConverter::class.asClassName())) {
                    if (field.type.inlineOf != null) {
                        val decodedTmpName = "${field.name.replace(".", "_")}Decoded"
                        addStatement(
                            "val $decodedTmpName = %T().decode(${ScalarFieldType.String.readMethod()})",
                            annotatedSerializer.toClassName()
                        )
                        //addStatement("${field.name} = ${ScalarFieldType.String.readMethod()}")
                    } else {
                        addStatement(
                            "${field.name} = ${ScalarFieldType.String.readMethod()}",
                            annotatedSerializer.toClassName()
                        )
                    }
                } else {
                    error("Not supported yet")
                }
            } ?: run {
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