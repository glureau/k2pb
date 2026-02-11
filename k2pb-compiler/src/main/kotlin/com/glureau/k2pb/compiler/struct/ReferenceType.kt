package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.customConverterType
import com.glureau.k2pb.compiler.poet.ProtoWireTypeClassName
import com.glureau.k2pb.compiler.poet.readMessageExt
import com.glureau.k2pb.compiler.poet.writeMessageExt
import com.glureau.k2pb.runtime.DefaultCodecImpl
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
    val inlineAnnotatedCodec: KSType? = null,
) : FieldType

fun FunSpec.Builder.encodeReferenceType(
    fieldName: String,
    type: ReferenceType,
    tag: Int?,
    annotatedCodec: KSType?,
    forceEncodeDefault: Boolean = false,
) {
    (annotatedCodec ?: type.inlineAnnotatedCodec)?.let { annCodec ->
        val fieldAccess = buildString {
            append(fieldName)
            if (type.inlineName != null) {
                if (type.isNullable == true) append("?")
                append(".${type.inlineName}")
            }
        }

        val checkNullability =
            type.isNullable || (type.inlineOf as? ReferenceType)?.isNullable == true
        if (checkNullability) {
            beginControlFlow("if ($fieldAccess != null)")
        }
        val encodedTmpName = "${fieldName.replace(".", "_")}Encoded"
        addStatement(
            "val $encodedTmpName = %T().encode(${fieldAccess.replace("?", "")}, %T(protoCodec))",
            annCodec.toClassName(),
            DefaultCodecImpl::class.asClassName()
        )
        annCodec.customConverterType()?.let { customType ->
            if (tag != null) {
                addCode(customType.safeWriteMethod(encodedTmpName, tag, forceEncodeDefault))
            } else {
                addCode(customType.safeWriteMethodNoTag(encodedTmpName, forceEncodeDefault))
            }
            addStatement("")
        }
            ?: error("Not supported yet")

        if (checkNullability) {
            endControlFlow()
        }
    } ?: (type.inlineOf)?.let { inlinedType: FieldType ->
        val isInlineEnum = (inlinedType as? ReferenceType)?.isEnum == true
        val isInlinedInt = (inlinedType as? ScalarFieldType)?.protoType == ScalarType.int32
        val condition = mutableListOf<String>()
        if (isInlineEnum) condition += "$fieldName != ${type.className}(${(inlinedType as? ReferenceType)?.enumFirstEntry})"
        if (inlinedType is ScalarFieldType) condition += inlinedType.shouldEncodeDefault(fieldName + "." + type.inlineName)
        val checkNullability = type.isNullable || inlinedType.isNullable

        if (checkNullability) {
            beginControlFlow("if ($fieldName != null)")
        }

        if (!forceEncodeDefault && condition.isNotEmpty()) {
            beginControlFlow("if (${condition.joinToString(" && ")})")
        }
        if (tag != null) {
            val wireType = if (isInlineEnum || isInlinedInt) "VARINT" else "SIZE_DELIMITED"
            addStatement("writeInt(%T.$wireType.wireIntWithTag($tag))", ProtoWireTypeClassName)
        }
        beginControlFlow("with(protoCodec)")
        addStatement("encode(${fieldName}, %T::class)", type.className.copy(nullable = false))
        endControlFlow()

        if (!forceEncodeDefault && condition.isNotEmpty()) {
            endControlFlow() // if (condition)
        }

        if (checkNullability) {
            endControlFlow() // if (checkNullability)
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
            beginControlFlow("%M($tag)", writeMessageExt)
        } else if (tag != null) {
            addStatement("writeInt(%T.VARINT.wireIntWithTag($tag))", ProtoWireTypeClassName)
        }

        beginControlFlow("with(protoCodec)")
        addStatement(
            "encode(${fieldName}, %T::class)",
            type.className.copy(nullable = false)
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
    }
}

fun FunSpec.Builder.decodeReferenceTypeVariableDefinition(
    fieldName: String,
    type: ReferenceType,
    nullabilitySubField: NullabilitySubField?,
) {
    addStatement("var $fieldName: %T? = null", type.className)
    nullabilitySubField?.let {
        addNullabilityStatement(nullabilitySubField)
    }
}

fun FunSpec.Builder.decodeReferenceType(
    fieldName: String,
    fieldType: ReferenceType,
    fieldAnnotatedCodec: KSType?,
) {
    (fieldAnnotatedCodec ?: fieldType.inlineAnnotatedCodec)?.let { annotatedCodec ->
        val customCodecType = annotatedCodec.customConverterType()
        if (customCodecType != null) {
            val decodedTmpName = decodeInLocalVar(fieldName, annotatedCodec, customCodecType)
            if (fieldType.inlineOf != null) {
                if (fieldType.inlineOf.isNullable) {
                    addStatement("${fieldType.className.copy(nullable = false)}($decodedTmpName)")
                } else {
                    addStatement("$decodedTmpName?.let { ${fieldType.className.copy(nullable = false)}($decodedTmpName) }")
                }
            } else {
                // TODO: here generated code could be cleaned, decodedTmpName is useless.
                addCode(decodedTmpName, annotatedCodec.toClassName()) // TODO unused 2nd param?
            }
        } else {
            error("Not supported yet")
        }
    } ?: run {
        val useReadMessage = fieldType.inlineOf == null && fieldType.isEnum == false
        if (useReadMessage) {
            beginControlFlow("%M", readMessageExt)
        }
        beginControlFlow("with(protoCodec) {")
        addStatement("decode(%T::class)", fieldType.className.copy(nullable = false))
        endControlFlow()
        if (useReadMessage) {
            endControlFlow()
        }
    }
}

fun FunSpec.Builder.decodeInLocalVar(
    fieldName: String,
    annotatedCodec: KSType,
    encodedType: ScalarFieldType,
): String {
    val decodedTmpName = "${fieldName.replace(".", "_")}Decoded"
    addStatement(
        "val $decodedTmpName = %T().decode(${encodedType.readMethod()}, %T(protoCodec))",
        annotatedCodec.toClassName(),
        DefaultCodecImpl::class.asClassName()
    )
    return decodedTmpName
}