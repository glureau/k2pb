package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.TypeResolver
import com.glureau.k2pb.compiler.mapping.toProtobufComment

data class TypedField(
    override val comment: String?,
    val type: FieldType,
    override val name: String,
    val annotatedNumber: Int?,
) : FieldInterface {
    override fun toString(numberManager: NumberManager): String {
        var result = ""
        result += comment.toProtobufComment()
        result += when (type) {
            is ScalarType -> type.name
            // Protobuf name COULD be simplified in function of the location, but a bit more complex to implement and
            // both solutions are valid for protobuf.
            is ReferenceType -> TypeResolver.qualifiedNameToProtobufName[type.name] ?: type.name
            is ListType -> type.toString()
            is MapType -> type.toString()
            else -> error("unknown type $type")
        }

        result += " $name = ${numberManager.resolve(name, annotatedNumber)};"
        return result
    }
}