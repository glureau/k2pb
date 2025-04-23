package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoPolymorphism
import com.glureau.k2pb.annotation.ProtoPolymorphism.Child
import com.glureau.k2pb.annotation.ProtoMessage

abstract class AbstractClass {
    abstract val foo: Int
}

@ProtoMessage("AbstractSubClass")
data class AbstractSubClass(override val foo: Int, val bar: String) : AbstractClass()

// Could be defined in another module actually...
@ProtoPolymorphism(
    AbstractClass::class,
    name = "AbstractClass",
    oneOf = [Child(AbstractSubClass::class, 1)]
)
private object K2PBPolymorphismConfigHolder
