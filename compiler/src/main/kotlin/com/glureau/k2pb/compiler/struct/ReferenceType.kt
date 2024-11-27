package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.TypeResolver
import com.glureau.k2pb.compiler.mapping.InlinedTypeRecorder
import com.google.devtools.ksp.symbol.KSType

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

    TypeResolver.qualifiedNameToProtobufName[type.name]?.let { resolvedType: String ->
        append(resolvedType)
        return
    }

    InlinedTypeRecorder.getInlinedType(type.name)?.let { node: InlinedTypeRecorder.InlineNode ->
        appendFieldType(node.inlinedFieldType, type.inlineAnnotatedSerializer)
        return
    }

    Logger.warn("Nothing found for ${type.name}, or is it just an ENUM ?")
    append(type.name)
}