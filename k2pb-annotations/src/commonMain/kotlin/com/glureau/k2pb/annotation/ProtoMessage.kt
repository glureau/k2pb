package com.glureau.k2pb.annotation

/**
 * Compared to Ktx Serialization, this annotation has no link check (yet).
 *
 * AnnotationTarget.TYPEALIAS is usable for migration tricks.
 * A more suitable solution needs to be defined instead of that trick.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS)
public annotation class ProtoMessage(
    val name: String = "",
    val deprecatedFields: Array<DeprecatedField> = [],
)
