package com.glureau.k2pb.compiler.struct

fun TypedField.nameOrDefault(): String {
    return type.nameOrDefault(name)
}

fun FieldType.nameOrDefault(name: String): String {
    return return when (this) {
        is ListType -> name // default value will be the aggregating mutable list (empty)
        is MapType -> name // default value will be the aggregating mutable map (empty)
        is ReferenceType -> if (isNullable) name else "requireNotNull($name)"
        ScalarFieldType.Double,
        ScalarFieldType.DoubleNullable -> "$name ?: 0.0"

        ScalarFieldType.Float,
        ScalarFieldType.FloatNullable -> "$name ?: 0.0f"

        ScalarFieldType.Int,
        ScalarFieldType.IntNullable -> "$name ?: 0"

        ScalarFieldType.Short,
        ScalarFieldType.ShortNullable -> "$name ?: 0"

        ScalarFieldType.Char,
        ScalarFieldType.CharNullable -> "$name ?: 0.toChar()"

        ScalarFieldType.Long,
        ScalarFieldType.LongNullable -> "$name ?: 0"

        ScalarFieldType.Byte,
        ScalarFieldType.ByteNullable -> "$name ?: 0"

        ScalarFieldType.Boolean,
        ScalarFieldType.BooleanNullable -> "$name == true"

        ScalarFieldType.String,
        ScalarFieldType.StringNullable -> "$name ?: \"\""

        ScalarFieldType.ByteArray,
        ScalarFieldType.ByteArrayNullable -> "$name ?: byteArrayOf()"

        is ScalarFieldType -> {
            error("ScalarType not handled for protobuf serialization: $this")
        }
    }
}