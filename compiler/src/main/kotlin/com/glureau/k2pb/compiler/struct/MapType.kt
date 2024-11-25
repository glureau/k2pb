package com.glureau.k2pb.compiler.struct

data class MapType(
    val keyType: FieldType,
    val valueType: FieldType,
    override val isNullable: Boolean = false
) : FieldType

fun StringBuilder.appendMapType(type: MapType) {
    append("map<")
    appendFieldType(type.keyType, null)
    append(", ")
    appendFieldType(type.valueType, null)
    append(">")
}

fun StringBuilder.appendKotlinMapDefinition(type: MapType) = apply {
    append(
        "Map<${appendKotlinDefinition(type.keyType)}, ${appendKotlinDefinition(type.valueType)}>"
                + if (type.isNullable) "?" else ""
    )
}