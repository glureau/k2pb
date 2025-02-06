package com.glureau.k2pb.annotation

import com.glureau.k2pb.ProtoConstructor
import kotlin.reflect.KClass

/**
 * Compared to Ktx Serialization, this annotation has no link check (yet).
 */
@Target(AnnotationTarget.CLASS)
public annotation class ProtoMessage(
    val name: String = "",
    // Using ProtoConstructor::class here means that no constructor has been set
    // (Kotlin annotations are not supporting nullability)
    val constructor: KClass<ProtoConstructor<*>> = ProtoConstructor::class,
)
