package com.glureau.k2pb.annotation

import com.glureau.k2pb.CustomSerializer
import com.glureau.k2pb.CustomStringSerializer
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
annotation class ProtoStringSerializer(
    val serializer: KClass<out CustomStringSerializer<*>>,
)

@Target(AnnotationTarget.PROPERTY)
annotation class ProtoCustomSerializer(
    val serializer: KClass<out CustomSerializer<*, *>>,
)

enum class FormatType { // https://protobuf.dev/programming-guides/proto3/#scalar
    double,
    float,
    int32,
    int64,
    bool,
    string,
    bytes,
    message,
}
