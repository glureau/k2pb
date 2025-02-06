package com.glureau.k2pb.compiler.struct

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName

sealed interface FieldType {
    val isNullable: Boolean
    val typeName: TypeName
}

fun StringBuilder.appendKotlinDefinition(type: FieldType): String {
    return when (type) {
        is ScalarFieldType -> type.typeName.canonicalName
        is ReferenceType -> type.typeName.toString()
        is ListType -> appendKotlinListDefinition(type).toString()
        is MapType -> appendKotlinMapDefinition(type).toString()
    }
}

fun FieldType.readNoTag(): CodeBlock =
    when (this) {
        is ScalarFieldType -> readMethodNoTag()
// TODO: improve this very basic implementation that uses requireNotNul...
        is ReferenceType -> CodeBlock.of("with(protoSerializer) { requireNotNull(decode(%T::class)) }", typeName)

        else -> TODO("Doesn't support readNoTag on $this")
    }
