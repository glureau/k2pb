package com.glureau.k2pb.compiler.struct
fun TypedField.nameOrDefault(): String {
    return when (type) {
        is ListType -> "$name ?: emptyList()"
        is MapType -> "$name ?: emptyMap()"
        is ReferenceType -> if (type.isNullable) name else "requireNotNull($name)"
        ScalarFieldType.Double -> "$name ?: 0.0"
        ScalarFieldType.Float -> "$name ?: 0.0f"
        ScalarFieldType.Int -> "$name ?: 0"
        ScalarFieldType.Short -> "$name ?: 0"
        ScalarFieldType.Char -> "$name ?: 0.toChar()"
        ScalarFieldType.Long -> "$name ?: 0"
        ScalarFieldType.Byte -> "$name ?: 0"
        ScalarFieldType.Boolean -> "$name ?: false"
        ScalarFieldType.String -> "$name ?: \"\""
        ScalarFieldType.ByteArray -> "$name ?: byteArrayOf()"

        is ScalarFieldType -> {
            error("ScalarType not handled for protobuf serialization: $type")
        }
    }
}