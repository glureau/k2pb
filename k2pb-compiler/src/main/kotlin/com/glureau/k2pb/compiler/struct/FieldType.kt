package com.glureau.k2pb.compiler.struct

import com.squareup.kotlinpoet.CodeBlock

sealed interface FieldType {
    val isNullable: Boolean
}

fun StringBuilder.appendKotlinDefinition(type: FieldType): String {
    return when (type) {
        is ScalarFieldType -> type.kotlinClass.canonicalName
        is ReferenceType -> type.className.toString()
        is ListType -> appendKotlinListDefinition(type).toString()
        is MapType -> appendKotlinMapDefinition(type).toString()
    }
}

fun FieldType.readNoTag(): CodeBlock =
    when (this) {
        is ScalarFieldType -> readMethodNoTag()
// TODO: improve this very basic implementation that uses requireNotNul...
        is ReferenceType -> CodeBlock.of("with(protoSerializer) { requireNotNull(decode(%T::class)) }", className)

        else -> TODO("Doesn't support readNoTag on $this")
    }
