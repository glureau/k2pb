package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.poet.readMessageExt
import com.glureau.k2pb.compiler.poet.writeMessageExt
import com.squareup.kotlinpoet.FunSpec

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

fun FunSpec.Builder.encodeMapType(fieldName: String, type: MapType, tag: Int) {
    beginControlFlow("instance.${fieldName}.forEach")
    beginControlFlow("%M($tag)", writeMessageExt)
    addCode(type.keyType.write("it.key", 1))
    addStatement("")

    addCode(type.valueType.write("it.value", 2))
    addStatement("")

    endControlFlow() // writeMessage of the item
    endControlFlow() // forEach
}

fun FunSpec.Builder.decodeMapTypeVariableDefinition(fieldName: String, type: MapType) {
    val typeName = StringBuilder().appendKotlinDefinition(type)
    addStatement("val ${fieldName}: Mutable${typeName} = mutableMapOf()")
}

fun FunSpec.Builder.decodeMapType(fieldName: String, type: MapType) {
    beginControlFlow("%M()", readMessageExt)
    addStatement("readTag()") // Should be 1
    addCode("val key = ")
    addCode(type.keyType.readNoTag())
    addStatement("")

    addStatement("readTag()") // Should be 2
    addCode("val value = ")
    addCode(type.valueType.readNoTag())
    addStatement("")
    addStatement("${fieldName}[key] = value")
    endControlFlow()
}