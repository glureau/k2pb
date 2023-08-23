package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.TypeResolver
import com.glureau.k2pb.compiler.mapping.InlinedTypeRecorder
import com.glureau.k2pb.compiler.sharedOptions

data class ReferenceType(val name: String) : FieldType {
    override fun toString(): String {
        // Protobuf name COULD be simplified in function of the location, but a bit more complex to implement and
        // both solutions are valid for protobuf.

        val resolvedType = TypeResolver.qualifiedNameToProtobufName[name]
            ?: InlinedTypeRecorder.getInlinedType(name)

        return resolvedType?.toString()
            ?: sharedOptions.replace(name)
            ?: (name.also {
                Logger.warn("Nothing found for $name, or is it just an ENUM ?")
            })
    }
}