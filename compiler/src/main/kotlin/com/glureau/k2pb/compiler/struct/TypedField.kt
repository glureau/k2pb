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
    appendFieldType(field.type)
    append(" ")
    append(field.annotatedName ?: field.name)
    append(" = ")
    append(numberManager.resolve(field.name, field.annotatedNumber))
    appendLine(";")
}

fun FunSpec.Builder.encodeTypedField(field: TypedField) {
    val tag = field.protoNumber
    when (field.type) {
        is ListType -> StringBuilder().appendKotlinListDefinition(field.type) // TODO
        is MapType -> Unit // TODO
        is ReferenceType -> {
            /*
            // Inelegant approach for inlining...
            field.type.inlineOf?.let {
                if (it is ScalarFieldType) {
                    encodeTypedField(
                        field.copy(
                            type = it,
                            name = (field.annotatedName ?: field.name) + "." + field.type.inlineName,
                            annotatedSerializer = field.annotatedSerializer ?: field.type.inlineAnnotatedSerializer
                        )
                    )
                    return
                } else {
                    TODO("Handle non scalar inlined types!")
                }
            }
            */
            beginControlFlow("%M($tag) {", writeMessageExt)
            beginControlFlow("with(delegate) {")
            addStatement("encode(instance.${field.name}, ${field.type.name}::class)")
            endControlFlow()
            endControlFlow()
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
    val typeName = StringBuilder().appendKotlinDefinition(field.type)
    // TODO: var only on non-mutable fields, to support packed properly
    addStatement("var ${field.name}: ${typeName}? = null")
}

fun FunSpec.Builder.decodeTypedField(field: TypedField) {
    when (field.type) {
        is ListType -> Unit
        is MapType -> Unit
        is ReferenceType -> {
            beginControlFlow("${field.name} = %M", readMessageExt)
            beginControlFlow("with(delegate) {")
            addStatement("decode(${field.type.name}::class)")
            endControlFlow()
            endControlFlow()
        }

        is ScalarFieldType -> {
            addStatement("${field.name} = ${field.type.readMethod()}")
        }
    }
}