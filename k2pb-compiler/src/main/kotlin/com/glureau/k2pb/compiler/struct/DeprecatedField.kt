package com.glureau.k2pb.compiler.struct

import com.squareup.kotlinpoet.ClassName

/**
 * Copy of [com.glureau.k2pb.annotation.DeprecatedField]
 */
sealed interface IDeprecatedField {
    val protoName: String
    val protoNumber: Int
    val protoType: String
    val deprecationReason: String?
    val publishedInProto: Boolean
}

data class DeprecatedField(
    override val protoName: String,
    override val protoNumber: Int,
    override val protoType: String,
    override val deprecationReason: String?,
    override val publishedInProto: Boolean,
    val migrationDecoder: ClassName?,
    val migrationTargetClass: ClassName?,
) : IDeprecatedField

data class DeprecatedNullabilityField(
    override val protoName: String,
    override val protoNumber: Int,
    override val deprecationReason: String?,
    override val publishedInProto: Boolean,
) : IDeprecatedField {
    override val protoType: String = nullabilityClass
}
