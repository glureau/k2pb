package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.Resolver
import com.glureau.k2pb.compiler.struct.OneOfField
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.TypedField
import com.google.devtools.ksp.common.impl.KSNameImpl
import com.squareup.kotlinpoet.ClassName
import java.util.Locale

fun classNamesToOneOfField(
    fieldName: String,
    subclassesWithProtoNumber: List<Pair<ClassName, Int>>,
    deprecateOneOf: List<OneOfField.DeprecatedField>
) =
    listOf(
        OneOfField(
            comment = null,
            name = fieldName.replaceFirstChar { it.lowercase(Locale.US) },
            protoNumber = 1,
            deprecatedFields = deprecateOneOf,
            activeFields = subclassesWithProtoNumber.map { (childClassName, number) ->
                TypedField(
                    comment = null,
                    type = ReferenceType(
                        className = childClassName,
                        annotatedProtoName = Resolver.getClassDeclarationByName(KSNameImpl.getCached(childClassName.canonicalName))
                            ?.annotatedProtoNameOrNull,
                        name = childClassName.canonicalName,
                        isNullable = false,
                        isEnum = false,
                    ),
                    name = childClassName.simpleName.replaceFirstChar { it.lowercase(Locale.UK) },
                    protoNumber = number,
                    annotatedName = null,
                    annotatedConverter = null,
                    annotatedNullableMigration = null,
                    nullabilitySubField = null,
                )
            }
        ))