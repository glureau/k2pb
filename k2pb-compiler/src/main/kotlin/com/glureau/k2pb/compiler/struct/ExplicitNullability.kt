package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.protofile.appendFieldType
import com.glureau.k2pb.compiler.writeProtobufFile
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.FunSpec

private enum class ExplicitNullability { UNSPECIFIED, NULL, NOT_NULL }

fun emitNullabilityProto(environment: SymbolProcessorEnvironment) {
    environment.writeProtobufFile(
        """
            |syntax = "proto3";
            |
            |package com.glureau.k2pb;
            |
            |option java_outer_classname = "AnnotatedClassProto";
            |
            |
            |// Ensures backward compatibility, when adding a new nullable field, we want to be able to distinguish
            |// between the absence of the field (old format read) and the default value (ex "" for String).
            |
            |enum K2PBNullability {
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
        packageName = "com.glureau.k2pb",
        fileName = "K2PBNullability",
        dependencies = emptyList(),
    )
}

fun FunSpec.Builder.addNullabilityStatement(nullabilitySubField: NullabilitySubField) {
    addStatement("var ${nullabilitySubField.fieldName}: Boolean = false")
}

fun FunSpec.Builder.decodeNullability(nullabilitySubField: NullabilitySubField) {
    decodeScalarType(nullabilitySubField.fieldName, ScalarFieldType.Boolean, null)
}


fun StringBuilder.appendNullabilityField(nullabilitySubField: NullabilitySubField) {
    appendFieldType(ScalarFieldType.Boolean, null)
    append(" ")
    append(nullabilitySubField.fieldName)
    append(" = ")
    append(nullabilitySubField.protoNumber)
    appendLine(";")
}
