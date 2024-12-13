package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.NullableStringConverter
import com.glureau.k2pb.StringConverter
import com.glureau.k2pb.compiler.struct.FieldType
import com.glureau.k2pb.compiler.struct.ListType
import com.glureau.k2pb.compiler.struct.MapType
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName

fun StringBuilder.appendFieldType(type: FieldType, annotatedSerializer: KSType?) {
    if (annotatedSerializer != null && annotatedSerializer.declaration is KSClassDeclaration) {
        val annotatedSerializerDecl = annotatedSerializer.declaration as KSClassDeclaration
        val parents = annotatedSerializerDecl.superTypes.map { it.resolve().toClassName() }
        if (parents.contains(StringConverter::class.asClassName())) {
            append("string")
            return
        }
        if (parents.contains(NullableStringConverter::class.asClassName())) {
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
