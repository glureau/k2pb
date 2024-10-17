package com.glureau.k2pb.annotation

/**
 * Compared to Ktx Serialization, this annotation has no link check (yet).
 */
@Target(AnnotationTarget.CLASS)
annotation class ProtoMessage(
    val name: String = ""
)
