package com.glureau.sample

import com.glureau.k2pb.ProtoPolymorphism
import com.glureau.k2pb.ProtoPolymorphism.Pair
import com.glureau.k2pb.annotation.ProtoMessage

abstract class AbstractClass {
    abstract val foo: Int
}

@ProtoMessage("AbstractSubClass")
data class AbstractSubClass(override val foo: Int, val bar: String) : AbstractClass()

// Could be defined in another module actually...
@ProtoPolymorphism(
    AbstractClass::class,
    [Pair(AbstractSubClass::class, 1)]
)
private object K2PBPolymorphismConfigHolder
