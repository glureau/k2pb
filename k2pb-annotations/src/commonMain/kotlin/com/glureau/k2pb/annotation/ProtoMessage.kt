package com.glureau.k2pb.annotation

import kotlin.reflect.KClass

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
    val deprecatedNullabilityFields: Array<DeprecatedNullabilityField> = [],
    /**
     * If not specified, the sealed children will be numbered from 1..n, by alphabetical order.
     * Adding a new sealed child is likely to break the numbering of existing ones.
     */
    val sealedProtoNumbers: Array<SealedChild> = [],
) {
    public annotation class SealedChild(
        val kClass: KClass<*>,
        val number: Int,
    )
}
