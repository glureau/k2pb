package com.glureau.k2pb.annotation

import com.glureau.k2pb.CustomConverter
import com.glureau.k2pb.CustomStringConverter
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
public annotation class ProtoStringSerializer(
    val serializer: KClass<out CustomStringConverter<*>>,
)

@Target(AnnotationTarget.PROPERTY)
public annotation class ProtoCustomSerializer(
    val serializer: KClass<out CustomConverter<*, *>>,
)

public enum class FormatType { // https://protobuf.dev/programming-guides/proto3/#scalar
    double,
    float,
    int32,
    int64,
    bool,
    string,
    bytes,
    message,
}
