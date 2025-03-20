package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.annotation.UnspecifiedBehavior
import com.glureau.k2pb.compiler.compileOptions
import com.glureau.k2pb.compiler.writeProtobufFile
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec

val nullabilityPackage = "com.glureau.k2pb"
val nullabilityClass = "K2PBNullability"
val nullabilityQualifiedName = "$nullabilityPackage.$nullabilityClass"
val nullabilityImport = "${nullabilityPackage.replace(".", "/")}/$nullabilityClass"
val K2PBNullabilityClassName = ClassName(nullabilityPackage, nullabilityClass)

fun emitNullabilityProto(environment: SymbolProcessorEnvironment) {
    environment.writeProtobufFile(
        """
            |syntax = "proto3";
            |
            |package $nullabilityPackage;
            |
            |option java_outer_classname = "$nullabilityClass${compileOptions.javaOuterClassnameSuffix}";
            |
            |// Ensures backward compatibility, when adding a new nullable field, we want to be able to distinguish
            |// between the absence of the field (old format read) and the default value (ex "" for String).
            |
            |enum $nullabilityClass {
            |  // A nullable field has not been explicitly defined, probably an evolution from a previous format.
            |  // In this case, the associated field will be returned without protobuf default value.
            |  // Example, a new nullable enum has been added, the default protobuf value would have been
            |  // technically the first enum entry
            |  // K2PB will return the associated value without any requirement.
            |  UNSPECIFIED = 0;
            |  
            |  // A nullable field has been explicitly set to NULL.
            |  // K2PB ignores the associated field, effectively returning a null for the value.
            |  NULL = 1;
            |  
            |  // A nullable field has been explicitly set to a non-null value.
            |  // The associated value could be the protobuf default ("" != null).
            |  // During deserialization, K2PB requires that the associated value is not null, or else throws.
            |  NOT_NULL = 2;
            |}
            """.trimMargin().toByteArray(),
        packageName = "k2pb.com.glureau.k2pb",
        fileName = nullabilityClass,
        dependencies = emptyList(),
    )
}

fun FunSpec.Builder.addNullabilityStatement(nullabilitySubField: NullabilitySubField) {
    addStatement(
        "var ${nullabilitySubField.fieldName}: %T = %T.UNSPECIFIED",
        K2PBNullabilityClassName,
        K2PBNullabilityClassName
    )
}

fun FunSpec.Builder.decodeNullability(nullabilitySubField: NullabilitySubField) {
    //decodeScalarType(nullabilitySubField.fieldName, ScalarFieldType.Boolean, null)
    addStatement(
        "${nullabilitySubField.fieldName} = " +
                "%T.entries.getOrElse(${ScalarFieldType.Int.readMethodNoTag()}) { %T.UNSPECIFIED }",
        K2PBNullabilityClassName,
        K2PBNullabilityClassName,
    )
}


fun StringBuilder.appendNullabilityField(nullabilitySubField: NullabilitySubField) {
    //appendFieldType(ScalarFieldType.Boolean, null)
    append(nullabilityQualifiedName)
    append(" ")
    append(nullabilitySubField.fieldName)
    append(" = ")
    append(nullabilitySubField.protoNumber)
    appendLine(";")
}


fun FunSpec.Builder.buildNullable(
    nullabilitySubField: NullabilitySubField,
    unspecifiedDefault: String,
    notNull: String
): String {
    return """when (${nullabilitySubField.fieldName}) {
        |  $nullabilityClass.UNSPECIFIED -> ${
        when (nullabilitySubField.unspecifiedBehavior) {
            UnspecifiedBehavior.NULL -> "null"
            UnspecifiedBehavior.DEFAULT -> unspecifiedDefault
        }
    }
        |  $nullabilityClass.NULL -> null
        |  $nullabilityClass.NOT_NULL -> requireNotNull($notNull)
        |}
    """.trimMargin()
}
