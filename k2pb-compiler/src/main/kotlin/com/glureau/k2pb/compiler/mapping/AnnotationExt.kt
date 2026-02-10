package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.CustomConverter
import com.glureau.k2pb.annotation.NullabilityMigration
import com.glureau.k2pb.annotation.ProtoField
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.annotation.ProtoPolymorphism
import com.glureau.k2pb.compiler.getArg
import com.glureau.k2pb.compiler.struct.DeprecatedField
import com.glureau.k2pb.compiler.struct.DeprecatedNullabilityField
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.glureau.k2pb.annotation.DeprecatedField as AnnotationDeprecatedField
import com.glureau.k2pb.annotation.DeprecatedNullabilityField as AnnotationDeprecatedNullabilityField


fun KSAnnotated.protoMessageAnnotation(): KSAnnotation? =
    annotations.firstOrNull { it.shortName.asString() == ProtoMessage::class.simpleName }

fun KSAnnotated.protoPolymorphismAnnotation(): KSAnnotation? =
    annotations.firstOrNull { it.shortName.asString() == ProtoPolymorphism::class.simpleName }

fun KSAnnotated.protoFieldAnnotation(): KSAnnotation? =
    annotations.firstOrNull { it.shortName.asString() == ProtoField::class.simpleName }

fun KSAnnotated.customConverter(): KSType? = protoFieldAnnotation()
    ?.getArg<KSType?>(ProtoField::converter)
    ?.takeIf { it.toClassName() != CustomConverter::class.asClassName() }

fun KSAnnotated.nullabilityMigration(): NullabilityMigration? = protoFieldAnnotation()
    ?.getArg<KSClassDeclaration?>(ProtoField::nullabilityMigration)
    ?.let { NullabilityMigration.valueOf(it.simpleName.getShortName()) }

fun KSAnnotated.nullabilityNumber(): Int? = protoFieldAnnotation()
    ?.getArg<Int?>(ProtoField::nullabilityNumber)
    ?.takeIf { it >= 0 } // Remove default value (-1)

fun KSAnnotation.mapToDeprecatedField(): DeprecatedField {
    val migrationDecoderType = getArg<KSType?>(AnnotationDeprecatedField::migrationDecoder)
    val migrationDecoderDeclaration = migrationDecoderType?.declaration as? KSClassDeclaration
    val migrationDecoderSuper =
        migrationDecoderDeclaration?.superTypes?.firstOrNull() as? KSTypeReference
    val migrationDecoderParameterType = migrationDecoderSuper?.resolve()?.arguments?.firstOrNull()?.type
    val migrationDecoderParameterClassName = migrationDecoderParameterType?.resolve()?.toClassName()

    val protoName = getArg<String>(AnnotationDeprecatedField::protoName)
    val protoType = getArg<String?>(AnnotationDeprecatedField::protoType)
        ?.takeIf { it.isNotBlank() }
        ?: protoName

    return DeprecatedField(
        protoName = protoName,
        protoNumber = getArg<Int>(AnnotationDeprecatedField::protoNumber),
        protoType = protoType,
        deprecationReason = getArg<String?>(AnnotationDeprecatedField::deprecationReason),
        publishedInProto = getArg<Boolean?>(AnnotationDeprecatedField::publishedInProto) ?: true,
        migrationDecoder = migrationDecoderType?.toClassName(),
        migrationTargetClass = migrationDecoderParameterClassName
    )
}


fun KSAnnotation.mapToDeprecatedNullabilityField(): DeprecatedNullabilityField {
    val targetName = getArg<String>(AnnotationDeprecatedNullabilityField::protoName)
    val protoName = nullabilityNameForField(targetName)
    return DeprecatedNullabilityField(
        protoName = protoName,
        protoNumber = getArg<Int>(AnnotationDeprecatedNullabilityField::protoNumber),
        deprecationReason = getArg<String?>(AnnotationDeprecatedNullabilityField::deprecationReason),
        publishedInProto = getArg<Boolean?>(AnnotationDeprecatedNullabilityField::publishedInProto) ?: true,
    )
}
