package com.glureau.k2pb.compiler.struct

sealed interface FieldInterface {
    val comment: String?
    val name: String
    val protoNumber: Int
}

fun StringBuilder.appendField(indentLevel: Int, field: FieldInterface, numberManager: NumberManager) {
    when (field) {
        is TypedField -> appendTypedField(indentLevel, field, numberManager)
        is OneOfField -> appendOneOfField(indentLevel, field, numberManager)
    }
}