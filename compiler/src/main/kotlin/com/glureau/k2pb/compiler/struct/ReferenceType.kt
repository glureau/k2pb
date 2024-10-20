package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.TypeResolver
import com.glureau.k2pb.compiler.mapping.InlinedTypeRecorder
import com.glureau.k2pb.compiler.sharedOptions

data class ReferenceType(val name: String, val isNullable: Boolean) : FieldType

fun StringBuilder.appendReferenceType(type: ReferenceType) {
    // Protobuf name COULD be simplified in function of the location, but a bit more complex to implement and
    // both solutions are valid for protobuf.

    TypeResolver.qualifiedNameToProtobufName[type.name]?.let { resolvedType: String ->
        append(resolvedType)
        return
    }

    InlinedTypeRecorder.getInlinedType(type.name)?.let { inlinedType: FieldType ->
        appendFieldType(inlinedType)
        return
    }

    sharedOptions.replace(type.name)?.let { replacement: String ->
        append(replacement)
        return
    }

    Logger.warn("Nothing found for ${type.name}, or is it just an ENUM ?")
    append(type.name)
}