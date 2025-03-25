package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoField
import com.glureau.k2pb.annotation.ProtoMessage

@ProtoMessage(name = "MyAnnotatedClass")
data class AnnotatedClass(
    @ProtoField(name = "a")
    val fieldA: Int,
    @ProtoField(name = "b", number = 4)
    val fieldB: Int,
    @ProtoField(number = 3)
    val c: String,
    // WARNING: here we know the last ProtoNumber was a 4, so we'll expect a 5,
    // this is to avoid mixing param index and number.
    // In case of issue add the @ProtoNumber annotation to be explicit.
    val d: String,
)
