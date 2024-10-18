package com.glureau.k2pb.annotation

@Target(AnnotationTarget.PROPERTY)
annotation class ProtoName(
    val name: String
)
