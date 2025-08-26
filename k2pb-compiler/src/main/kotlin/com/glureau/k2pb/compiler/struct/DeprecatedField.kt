package com.glureau.k2pb.compiler.struct

import com.squareup.kotlinpoet.ClassName

/**
 * Copy of [com.glureau.k2pb.annotation.DeprecatedField]
 */
data class DeprecatedField(
    val protoName: String,
    val protoNumber: Int,
    val protoType: String,
    val deprecationReason: String?,
    val publishedInProto: Boolean,
    val migrationDecoder: ClassName?,
    val migrationTargetClass: ClassName?,
)