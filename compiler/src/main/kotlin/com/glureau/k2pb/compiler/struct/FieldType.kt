package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.CustomStringConverter
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

sealed interface FieldType {
    val isNullable: Boolean
}

fun StringBuilder.appendFieldType(type: FieldType, annotatedSerializer: KSType?) {
    if (annotatedSerializer != null && annotatedSerializer.declaration is KSClassDeclaration) {
        val annotatedSerializerDecl = annotatedSerializer.declaration as KSClassDeclaration
        val parents = annotatedSerializerDecl.superTypes.map { it.resolve().toClassName() }
        if (parents.contains(CustomStringConverter::class.asClassName())) {
            append("string")
            return
        }
        error("Annotated custom serializer not supported yet: $annotatedSerializer")
    }
    when (type) {
        is ScalarFieldType -> appendScalarType(type)
        is ReferenceType -> appendReferenceType(type)
        is ListType -> appendListType(type, annotatedSerializer)
        is MapType -> appendMapType(type)
    }
}

fun StringBuilder.appendKotlinDefinition(type: FieldType): String {
    return when (type) {
        is ScalarFieldType -> type.kotlinClass.canonicalName
        is ReferenceType -> type.name
        is ListType -> appendKotlinListDefinition(type).toString()
        is MapType -> appendKotlinMapDefinition(type).toString()
    }
}

fun FieldType.readNoTag(): CodeBlock =
    when (this) {
        is ScalarFieldType -> readMethodNoTag()
        else -> TODO()
    }

fun FieldType.write(name: String, tag: Int): CodeBlock =
    when (this) {
        is ScalarFieldType -> safeWriteMethod(name, tag, null)
        else -> TODO()
    }