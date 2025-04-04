package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.decapitalizeUS
import com.glureau.k2pb.compiler.poet.readMessageExt
import com.glureau.k2pb.compiler.poet.writeMessageExt
import com.squareup.kotlinpoet.FunSpec

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
    val fields: List<FieldInterface>,
) : FieldInterface

fun FunSpec.Builder.encodeOneOfField(instanceName: String, oneOfField: OneOfField) {
    beginControlFlow("when ($instanceName)")
    oneOfField.fields.forEach { subclass ->
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

    oneOfField.fields.forEach { subclass ->
        subclass as TypedField
        subclass.type as ReferenceType
        addStatement("%L -> decode(%T::class)", subclass.protoNumber, subclass.type.className)
    }
    addStatement("else -> error(\"Ignoring unknown tag: \$oneOfTag\")")
    endControlFlow()
    endControlFlow()
    endControlFlow()
}
