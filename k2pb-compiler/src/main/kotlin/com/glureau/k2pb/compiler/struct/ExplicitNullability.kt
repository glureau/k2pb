package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.ExplicitNullability
import com.glureau.k2pb.annotation.NullableMigration
import com.glureau.k2pb.compiler.poet.ProtoIntegerTypeDefault
import com.glureau.k2pb.compiler.writeProtobufFile
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.asClassName

val K2PBNullabilityClassName =
    ExplicitNullability::class.asClassName()// ClassName(nullabilityPackage, nullabilityClass)
private val nullabilityPackage = K2PBNullabilityClassName.packageName
val nullabilityClass = K2PBNullabilityClassName.simpleName
val nullabilityQualifiedName = K2PBNullabilityClassName.canonicalName // com.glureau.k2pb.ExplicitNullability
val nullabilityImport = "${nullabilityPackage.replace(".", "/")}/$nullabilityClass"

fun emitNullabilityProto(environment: SymbolProcessorEnvironment) {
    // See ExplicitNullability class in k2pb-runtime
    environment.writeProtobufFile(
        """
            |syntax = "proto3";
            |
            |package $nullabilityPackage;
            |
            |option java_outer_classname = "K2PBConstants";
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
            |  UNKNOWN = 0;
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
        packageName = "k2pb.$nullabilityPackage",
        fileName = nullabilityClass,
        dependencies = emptyList(),
    )
}

fun FunSpec.Builder.encodeNullability(protoNumber: Int, isNull: Boolean) {
    val explicitNullability = if (isNull) ExplicitNullability.NULL else ExplicitNullability.NOT_NULL
    addStatement(
        "writeInt(value = %T.${explicitNullability.name}.ordinal, tag = $protoNumber, format = %T)",
        K2PBNullabilityClassName,
        ProtoIntegerTypeDefault,
    )
}

fun FunSpec.Builder.addNullabilityStatement(nullabilitySubField: NullabilitySubField) {
    addStatement(
        "var ${nullabilitySubField.fieldName}: %T = %T.UNKNOWN",
        K2PBNullabilityClassName,
        K2PBNullabilityClassName,
    )
}

fun FunSpec.Builder.decodeNullability(nullabilitySubField: NullabilitySubField) {
    addStatement(
        "${nullabilitySubField.fieldName} = " +
                "%T.entries.getOrElse(${ScalarFieldType.Int.readMethodNoTag()}) { %T.UNKNOWN }",
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
    nameOrDefault: String,
): String {
    return """when (${nullabilitySubField.fieldName}) {
        |  $nullabilityClass.UNKNOWN -> ${
        when (nullabilitySubField.nullableMigration) {
            NullableMigration.NULL -> "null"
            NullableMigration.DEFAULT -> nameOrDefault
        }
    }
        |  $nullabilityClass.NULL -> null
        |  $nullabilityClass.NOT_NULL -> $nameOrDefault
        |}
    """.trimMargin()
}
