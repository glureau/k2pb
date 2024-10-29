package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage

//@ProtoMessage
abstract class AbstractClass {
    abstract val foo: Int
}

@ProtoMessage("AbstractSubClass")
data class AbstractSubClass(override val foo: Int, val bar: String) : AbstractClass()
