package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.TypeResolver
import com.glureau.k2pb.compiler.struct.ReferenceType

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
