package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.ProtoPolymorphism
import com.glureau.k2pb.compiler.poet.readMessageExt
import com.glureau.k2pb.compiler.poet.writeMessageExt
import com.squareup.kotlinpoet.FunSpec
import kotlin.math.max

// This is an explicit implementation of the oneOf field:
// The interface/abstract class is producing 1 proto file which has a message with 1 oneOf field.
// PROs:
// - Same code structure as an interface will have the same name in code and in generated code.
// - May have ability to handle nullability in this structure (to be tested, not available yet).
// - Reusability of the protobuf structure instead of inlining the oneOf in many places.
// CONs:
// - Requires 2 more bytes (tag for the unique oneOf field, and the size of the bytes which can take more than 1 byte)
//
// Note that the idea of inlining oneOf is just an idea for now, it's not clear if we can put a oneOf field
// in a message with something else (as fields after the oneOf will have proto numbers unaligned).
// I keep this draft as a note for myself, but it's unlikely to be implemented with current knowledge.

data class OneOfField(
    override val comment: String?,
    override val name: String,
    override val protoNumber: Int,
    val deprecatedFields: List<DeprecatedField>,
    val activeFields: List<FieldInterface>,
) : FieldInterface {
    /**
     * Copy of [ProtoPolymorphism.Deprecated]
     */
    data class DeprecatedField(
        val protoName: String,
        val protoNumber: Int,
        val deprecationReason: String,
        val publishedInProto: Boolean,
    )
}

fun FunSpec.Builder.encodeOneOfField(instanceName: String, oneOfField: OneOfField) {
    beginControlFlow("when ($instanceName)")
    oneOfField.activeFields.forEach { subclass ->
        subclass as TypedField
        subclass.type as ReferenceType
        beginControlFlow("is %T ->", subclass.type.className)
        beginControlFlow("%M(%L)", writeMessageExt, subclass.protoNumber)
        beginControlFlow("with(protoSerializer)")
        addStatement("encode($instanceName, %T::class)", subclass.type.className)
        endControlFlow()
        endControlFlow()
        endControlFlow()
    }
    endControlFlow()
}

fun FunSpec.Builder.decodeOneOfFieldVariableDefinition(oneOfField: OneOfField) {
    // No-op
}

fun FunSpec.Builder.decodeOneOfField(oneOfField: OneOfField) {
    addStatement("val oneOfTag = readTag()")
    beginControlFlow("return %M", readMessageExt)
    beginControlFlow("with(protoSerializer)")
    beginControlFlow("when (oneOfTag)")

    val maxProtoNumber = max(
        oneOfField.deprecatedFields.maxOfOrNull { it.protoNumber } ?: 1,
        oneOfField.activeFields.maxOfOrNull { it.protoNumber } ?: 1,
    )

    for (index in 1..maxProtoNumber) {
        val activeField = oneOfField.activeFields.firstOrNull { it.protoNumber == index }
        val deprecatedField = oneOfField.deprecatedFields.firstOrNull { it.protoNumber == index }
        when {
            activeField != null && deprecatedField != null ->
                error("Conflict, the protoNumber $index is used by both active and deprecated fields")

            activeField == null && deprecatedField == null -> {
                addComment(
                    "The protoNumber $index is not defined, if it's not used anymore " +
                            "consider using @ProtoPolymorphism.Deprecated annotation."
                )
            }

            activeField != null -> {
                activeField as TypedField
                activeField.type as ReferenceType
                addStatement("%L -> decode(%T::class)", activeField.protoNumber, activeField.type.className)
            }

            deprecatedField != null -> {
                addStatement(
                    "%L -> return@readMessage null // deprecated ${deprecatedField.protoName}",
                    deprecatedField.protoNumber
                )
            }
        }
    }

    addStatement("else -> error(\"Ignoring unknown tag: \$oneOfTag\")")
    endControlFlow()
    endControlFlow()
    endControlFlow()
}
