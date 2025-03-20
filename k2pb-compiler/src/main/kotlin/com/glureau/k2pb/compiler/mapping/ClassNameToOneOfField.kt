package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.struct.OneOfField
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.TypedField
import com.squareup.kotlinpoet.ClassName
import java.util.Locale

fun classNamesToOneOfField(fieldName: String, subclassesWithProtoNumber: Map<ClassName, Int>) =
    listOf(
        OneOfField(
            comment = null,
            name = fieldName.replaceFirstChar { it.lowercase(Locale.US) },
            protoNumber = 1,
            fields = subclassesWithProtoNumber.map { (childClassName, number) ->
                TypedField(
                    comment = null,
                    type = ReferenceType(
                        className = childClassName,
                        name = childClassName.canonicalName,
                        isNullable = false,
                        isEnum = false,
                    ),
                    name = childClassName.simpleName.replaceFirstChar { it.lowercase(Locale.UK) },
                    protoNumber = number,
                    annotatedName = null,
                    annotatedConverter = null,
                    annotatedUnspecifiedBehavior = null,
                    nullabilitySubField = null,
                )
            }
        ))