package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.CustomStringConverter
import com.glureau.k2pb.ProtoIntegerType
import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.TypeResolver
import com.glureau.k2pb.compiler.mapping.customSerializerType
import com.glureau.k2pb.compiler.poet.ProtoWireTypeClassName
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

data class ReferenceType(
    val name: String,
    override val isNullable: Boolean,
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
        }
    } ?: (type.inlineOf)?.let { inlinedType: FieldType ->
        //require(tag != null) { "Tag is mandatory for inlined type" }
        if (nullabilitySubField != null) {
            beginControlFlow("if ($fieldName != null)")
        }

        if (tag != null) {
            addStatement("writeInt(%T.SIZE_DELIMITED.wireIntWithTag($tag))", ProtoWireTypeClassName)
        }
        beginControlFlow("with(protoSerializer)")
        addStatement("encode(${fieldName}, ${type.name}::class) /* FF */")
        endControlFlow()
        if (nullabilitySubField != null) {
            endControlFlow()
            beginControlFlow("else")
            addStatement(
                "writeInt(value = 1, tag = ${nullabilitySubField.protoNumber}, format = %T.DEFAULT)",
                ProtoIntegerType::class.asClassName()
            )

            endControlFlow()
        }
    } ?: run {
        beginControlFlow("%M($tag)", writeMessageExt)
        beginControlFlow("with(protoSerializer)")
        addStatement("encode(${fieldName}, ${type.name}::class)")
        endControlFlow()
        endControlFlow()
    }
}

fun FunSpec.Builder.decodeReferenceTypeVariableDefinition(
    fieldName: String,
    type: ReferenceType,
    annotatedSerializer: KSType?,
    nullabilitySubField: NullabilitySubField?
) {
    /*
    (annotatedSerializer ?: type.inlineAnnotatedSerializer)?.let { annSerializer ->
        val parents = (annSerializer.declaration as KSClassDeclaration)
            .superTypes
            .map { it.resolve().toClassName() }
        if (parents.contains(CustomStringConverter::class.asClassName())) {
            addStatement("var $fieldName: String? = null /* CUSTO */")
        } else {
            error("Not supported yet")
        }
    } ?: run {
        */

    addStatement("var $fieldName: ${type.name}? = null")
    nullabilitySubField?.let {
        addStatement("var ${nullabilitySubField.fieldName}: Boolean = false")
    }

    //}
}

fun FunSpec.Builder.decodeReferenceType(
    fieldName: String,
    fieldType: ReferenceType,
    fieldAnnotatedSerializer: KSType?,
) {
    (fieldAnnotatedSerializer ?: fieldType.inlineAnnotatedSerializer)?.let { annotatedSerializer ->
        val parents = (annotatedSerializer.declaration as KSClassDeclaration)
            .superTypes
            .map { it.resolve().toClassName() }
        if (parents.contains(CustomStringConverter::class.asClassName())) {
            val decodedTmpName = decodeInLocalVar(fieldName, annotatedSerializer)
            if (fieldType.inlineOf != null) {
                if (fieldType.inlineOf.isNullable == true) {
                    addStatement("${fieldType.name}($decodedTmpName) /* P */")
                } else {
                    addStatement("$decodedTmpName?.let { ${fieldType.name}($decodedTmpName) } /* O */")
                }
            } else {
                // TODO: here generated code could be cleaned, decodedTmpName is useless.
                addCode(decodedTmpName, annotatedSerializer.toClassName()) // TODO unused 2nd param?
            }
        } else {
            error("Not supported yet")
        }
    } ?: run {
        if (fieldType.inlineOf == null) {
            beginControlFlow("%M", readMessageExt)
        }
        beginControlFlow("with(protoSerializer) {")
        addStatement("decode(${fieldType.name}::class)")
        endControlFlow()
        if (fieldType.inlineOf == null) {
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