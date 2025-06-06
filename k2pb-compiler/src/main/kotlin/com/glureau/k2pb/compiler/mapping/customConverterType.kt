package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.NullableByteArrayConverter
import com.glureau.k2pb.NullableStringConverter
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

fun KSType?.customConverterType(): ScalarFieldType? {
    if (this != null && declaration is KSClassDeclaration) {
        val annotatedCodecDecl = declaration as KSClassDeclaration
        val parents = annotatedCodecDecl.superTypes.map { it.resolve().toClassName() }
        if (parents.contains(NullableStringConverter::class.asClassName())) {
            return ScalarFieldType.StringNullable
        }
        if (parents.contains(NullableByteArrayConverter::class.asClassName())) {
            return ScalarFieldType.ByteArrayNullable
        }
        error("Annotated custom codec not supported yet: $this")
    }
    return null
}
