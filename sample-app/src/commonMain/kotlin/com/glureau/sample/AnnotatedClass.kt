@file:OptIn(ExperimentalSerializationApi::class)

package com.glureau.sample

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
@SerialName("MyAnnotatedClass")
data class AnnotatedClass(
    @SerialName("a")
    val fieldA: Int,
    @ProtoNumber(4)
    @SerialName("b")
    val fieldB: Int,
    @ProtoNumber(3)
    val c: String,
    // WARNING: here we know the last ProtoNumber was a 4, so we'll expect a 5,
    // this is to avoid mixing param index and number.
    // In case of issue add the @ProtoNumber annotation to be explicit.
    val d: String,
)
