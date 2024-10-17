package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.annotation.ProtoName
import com.glureau.k2pb.annotation.ProtoNumber

@ProtoMessage("MyAnnotatedClass")
data class AnnotatedClass(
    @ProtoName("a")
    val fieldA: Int,
    @ProtoNumber(4)
    @ProtoName("b")
    val fieldB: Int,
    @ProtoNumber(3)
    val c: String,
    // WARNING: here we know the last ProtoNumber was a 4, so we'll expect a 5,
    // this is to avoid mixing param index and number.
    // In case of issue add the @ProtoNumber annotation to be explicit.
    val d: String,
)
