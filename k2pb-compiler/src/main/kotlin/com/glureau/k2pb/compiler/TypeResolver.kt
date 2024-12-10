package com.glureau.k2pb.compiler

// TODO: static TypeResolver is not a good idea (especially for tests)
object TypeResolver {
    val qualifiedNameToProtobufName = mutableMapOf<String, String>()
}