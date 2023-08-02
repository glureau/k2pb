package com.glureau.sample

import kotlinx.serialization.SerialName
import kotlinx.serialization.protobuf.ProtoNumber

@SerialName("MyAnnotatedClass")
data class AnnotatedClass(
    @SerialName("a")
    val fieldA: Int,
    @ProtoNumber(4)
    val fieldB: Int,
    @ProtoNumber(3)
    val fieldC: String,
)
