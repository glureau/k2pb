package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.CustomConverter
import com.glureau.k2pb.annotation.ProtoPolymorphism
import com.glureau.k2pb.annotation.NullableMigration
import com.glureau.k2pb.annotation.ProtoField
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.compiler.getArg
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName


fun KSAnnotated.protoMessageAnnotation(): KSAnnotation? =
    annotations.firstOrNull { it.shortName.asString() == ProtoMessage::class.simpleName }

fun KSAnnotated.protoPolymorphismAnnotation(): KSAnnotation? =
    annotations.firstOrNull { it.shortName.asString() == ProtoPolymorphism::class.simpleName }

fun KSAnnotated.protoFieldAnnotation(): KSAnnotation? =
    annotations.firstOrNull { it.shortName.asString() == ProtoField::class.simpleName }

fun KSAnnotated.customConverter(): KSType? = protoFieldAnnotation()
    ?.getArg<KSType?>(ProtoField::converter)
    ?.takeIf { it.toClassName() != CustomConverter::class.asClassName() }

fun KSAnnotated.nullableMigration(): NullableMigration? = protoFieldAnnotation()
    ?.getArg<KSType?>(ProtoField::nullabilityMigration)
    ?.let { NullableMigration.valueOf(it.declaration.simpleName.getShortName()) }
