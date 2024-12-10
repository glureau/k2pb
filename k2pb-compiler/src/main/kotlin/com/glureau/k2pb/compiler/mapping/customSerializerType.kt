package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.CustomStringConverter
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

fun KSType?.customSerializerType(): ScalarFieldType? {
    if (this != null && declaration is KSClassDeclaration) {
        val annotatedSerializerDecl = declaration as KSClassDeclaration
        val parents = annotatedSerializerDecl.superTypes.map { it.resolve().toClassName() }
        if (parents.contains(CustomStringConverter::class.asClassName())) {
            return ScalarFieldType.String
        }
        error("Annotated custom serializer not supported yet: $this")
    }
    return null
}