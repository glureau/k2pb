package com.glureau.k2pb.annotation

import com.glureau.k2pb.ProtoDecoder
import kotlin.reflect.KClass

/**
 * Deprecated annotation helps to support the removal of a Kotlin class while it's still declared in protobuf
 * files for retrocompatibility.
 * This annotation can't be used directly, see [ProtoMessage].
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
public annotation class DeprecatedField(
    /**
     * Proto name (the name previously declared in ProtoMessage) of the field.
     */
    val protoName: String,
    /**
     * The number of this field in the message.
     */
    val protoNumber: Int,
    /**
     * The original type of the field, if not set (empty) the protoName is used.
     * If set, it's used as is, without any validation.
     */
    val protoType: String = "",
    /**
     * Reasons of the deprecation, this will be copied into the proto file documentation.
     */
    val deprecationReason: String = "",
    /**
     * If true, the [protoName] is still used in the proto file (with [deprecationReason] as comment).
     * If false, the [protoName] and [protoNumber] are marked as **reserved** values.
     * https://protobuf.dev/programming-guides/proto3/#reserved
     */
    val publishedInProto: Boolean = true,

    val migrationDecoder: KClass<out ProtoDecoder<*>> = ProtoDecoder::class,
)

/**
 * Deprecated annotation helps to support the migration of a non-nullable field to a nullable field and vice-versa.
 * This annotation can't be used directly, see [ProtoMessage].
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
public annotation class DeprecatedNullabilityField(
    /**
     * Proto name (the name previously declared in ProtoMessage) of the target.
     */
    val protoName: String,
    /**
     * The number of the field in the one of message.
     */
    val protoNumber: Int,
    /**
     * Reasons of the deprecation, this will be copied into the proto file documentation.
     */
    val deprecationReason: String = "",
    /**
     * If true, the [protoName] is still used in the proto file (with [deprecationReason] as comment).
     * If false, the [protoName] and [protoNumber] are marked as **reserved** values.
     * https://protobuf.dev/programming-guides/proto3/#reserved
     */
    val publishedInProto: Boolean = true,
)
