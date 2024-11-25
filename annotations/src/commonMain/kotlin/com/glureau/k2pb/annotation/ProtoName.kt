package com.glureau.k2pb.annotation

@Target(AnnotationTarget.PROPERTY)
public annotation class ProtoName(
    val name: String
)
