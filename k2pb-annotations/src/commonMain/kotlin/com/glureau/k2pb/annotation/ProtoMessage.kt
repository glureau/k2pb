package com.glureau.k2pb.annotation

import kotlin.reflect.KClass

/**
 * Compared to Ktx Serialization, this annotation has no link check (yet).
 */
@Target(AnnotationTarget.CLASS)
public annotation class ProtoMessage(
    val name: String = "",
    val constructor: KClass<*> = Any::class,
)
