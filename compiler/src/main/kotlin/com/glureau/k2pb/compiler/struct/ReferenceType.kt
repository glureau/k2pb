package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.CustomStringConverter
import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.TypeResolver
import com.glureau.k2pb.compiler.mapping.customSerializerType
import com.glureau.k2pb.compiler.poet.ProtoIntegerTypeDefault
import com.glureau.k2pb.compiler.poet.ProtoWireTypeClassName
import com.glureau.k2pb.compiler.poet.readMessageExt
import com.glureau.k2pb.compiler.poet.writeMessageExt
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

data class ReferenceType(
    val className: ClassName,
    val name: String,
    override val isNullable: Boolean,
    val isEnum: Boolean,
    val enumFirstEntry: ClassName? = null,
    val inlineOf: FieldType? = null,
    val inlineName: String? = null,
    val inlineAnnotatedSerializer: KSType? = null,
) : FieldType

fun StringBuilder.appendReferenceType(type: ReferenceType) {
    // Protobuf name COULD be simplified in function of the location, but a bit more complex to implement and
    // both solutions are valid for protobuf.

    type.inlineOf?.let { inlined ->
        appendFieldType(inlined, type.inlineAnnotatedSerializer)
        return
    }

    TypeResolver.qualifiedNameToProtobufName[type.name]?.let { resolvedType: String ->
        append(resolvedType)
        return
    }

    Logger.warn("Nothing found for ${type.name}, or is it just an ENUM ?")
    append(type.name)
}

fun FunSpec.Builder.encodeReferenceType(
    fieldName: String,
    type: ReferenceType,
    tag: Int?,
    annotatedSerializer: KSType?,
    nullabilitySubField: NullabilitySubField?
) {
    (annotatedSerializer ?: type.inlineAnnotatedSerializer)?.let { annSerializer ->
        val fieldAccess = fieldName + (type.inlineName?.let { ".$it" } ?: "")

        val checkNullability =
            type.isNullable || (type.inlineOf as? ReferenceType)?.isNullable == true
        if (checkNullability) {
            beginControlFlow("if ($fieldAccess != null)")
        }
        val encodedTmpName = "${fieldName.replace(".", "_")}Encoded"
        addStatement("val $encodedTmpName = %T().encode($fieldAccess)", annSerializer.toClassName())
        annSerializer.customSerializerType()?.let { customType ->
            if (tag != null) {
                /* TODO: custom string converter nullability */
                addCode(customType.safeWriteMethod(encodedTmpName, tag, null))
            } else {
                addCode(customType.safeWriteMethodNoTag(encodedTmpName, null))
            }
            addStatement("")
        }
            ?: error("Not supported yet")

        if (checkNullability) {
            endControlFlow()
            if (nullabilitySubField != null) {
                beginControlFlow("else")
                addStatement(
                    "writeInt(value = 1, tag = ${nullabilitySubField.protoNumber}, format = %T)",
                    ProtoIntegerTypeDefault
                )
                endControlFlow()
            }
        }
    } ?: (type.inlineOf)?.let { inlinedType: FieldType ->
        val isInlineEnum = (inlinedType as? ReferenceType)?.isEnum == true
        val isInlinedInt = (inlinedType as? ScalarFieldType)?.protoType == ScalarType.int32
        val condition = mutableListOf<String>()
        if (nullabilitySubField != null) condition += "$fieldName != null"
        if (isInlineEnum) condition += "$fieldName != ${type.className}(${(inlinedType as? ReferenceType)?.enumFirstEntry})"

        if (condition.isNotEmpty()) {
            beginControlFlow("if (${condition.joinToString(" && ")})")
        }

        if (tag != null) {
            val wireType = if (isInlineEnum || isInlinedInt) "VARINT" else "SIZE_DELIMITED"
            addStatement("writeInt(%T.$wireType.wireIntWithTag($tag))", ProtoWireTypeClassName)
        }
        beginControlFlow("with(protoSerializer)")
        addStatement("encode(${fieldName}, %T::class) /* FF */", type.className)
        endControlFlow()

        if (condition.isNotEmpty()) {
            endControlFlow() // if (condition)
        }

        if (nullabilitySubField != null) {
            beginControlFlow("else")
            addStatement(
                "writeInt(value = 1, tag = ${nullabilitySubField.protoNumber}, format = %T)",
                ProtoIntegerTypeDefault
            )
            endControlFlow() // else
        }
    } ?: run {
        if (type.isNullable) {
            beginControlFlow("if ($fieldName != null)")
        }

        if (type.isEnum) {
            addStatement("// Enum should not be encoded if it's the default value")
            beginControlFlow("if ($fieldName != ${type.enumFirstEntry})")
            //addStatement("if ($fieldName == %T) return", type.enumFirstEntry!!)
            //addStatement("")
        }

        if (!type.isEnum) {
            beginControlFlow("%M($tag) /* TTT */ ", writeMessageExt)
        } else if (tag != null) {
            addStatement("writeInt(%T.VARINT.wireIntWithTag($tag))", ProtoWireTypeClassName)
        }

        beginControlFlow("with(protoSerializer)")
        addStatement(
            "encode(${fieldName}, %T::class)",
            type.className
        )
        endControlFlow() // with

        if (!type.isEnum) {
            endControlFlow() // writeMessage
        } else {
            endControlFlow() // if (enum)
        }

        if (type.isNullable) {
            endControlFlow() // if (!null)
        }

        if (nullabilitySubField != null) {
            beginControlFlow("else")
            addStatement(
                "writeInt(value = 1, tag = ${nullabilitySubField.protoNumber}, format = %T)",
                ProtoIntegerTypeDefault
            )
            endControlFlow() // else
        }
    }
}

fun FunSpec.Builder.decodeReferenceTypeVariableDefinition(
    fieldName: String,
    type: ReferenceType,
    nullabilitySubField: NullabilitySubField?
) {
    addStatement("var $fieldName: %T? = null", type.className)
    nullabilitySubField?.let {
        addStatement("var ${nullabilitySubField.fieldName}: Boolean = false")
    }
}

fun FunSpec.Builder.decodeReferenceType(
    fieldName: String,
    fieldType: ReferenceType,
    fieldAnnotatedSerializer: KSType?,
) {
    (fieldAnnotatedSerializer ?: fieldType.inlineAnnotatedSerializer)?.let { annotatedSerializer ->
        // TODO use the CustomStringConverter extension
        val parents = (annotatedSerializer.declaration as KSClassDeclaration)
            .superTypes
            .map { it.resolve().toClassName() }
        if (parents.contains(CustomStringConverter::class.asClassName())) {
            val decodedTmpName = decodeInLocalVar(fieldName, annotatedSerializer)
            if (fieldType.inlineOf != null) {
                if (fieldType.inlineOf.isNullable == true) {
                    addStatement("${fieldType.className}($decodedTmpName) /* P */")
                } else {
                    addStatement("$decodedTmpName?.let { ${fieldType.className}($decodedTmpName) } /* O */")
                }
            } else {
                // TODO: here generated code could be cleaned, decodedTmpName is useless.
                addCode(decodedTmpName, annotatedSerializer.toClassName()) // TODO unused 2nd param?
            }
        } else {
            error("Not supported yet")
        }
    } ?: run {
        val useReadMessage = fieldType.inlineOf == null && fieldType.isEnum == false
        if (useReadMessage) {
            beginControlFlow("%M", readMessageExt)
        }
        beginControlFlow("with(protoSerializer) {")
        addStatement("decode(%T::class)", fieldType.className)
        endControlFlow()
        if (useReadMessage) {
            endControlFlow()
        }
    }
}

fun FunSpec.Builder.decodeInLocalVar(
    fieldName: String,
    annotatedSerializer: KSType
): String {
    val decodedTmpName = "${fieldName.replace(".", "_")}Decoded"
    addStatement(
        "val $decodedTmpName = %T().decode(${ScalarFieldType.String.readMethod()})",
        annotatedSerializer.toClassName()
    )
    return decodedTmpName
}