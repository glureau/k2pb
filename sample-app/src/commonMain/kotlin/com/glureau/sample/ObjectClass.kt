package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage


@ProtoMessage
object ObjectClass {
    val foo: Int = 42 // Not serializable
}