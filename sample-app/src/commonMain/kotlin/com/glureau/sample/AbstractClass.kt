package com.glureau.sample

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class AbstractClass {
    abstract val foo: Int
}

@SerialName("AbstractSubClass")
@Serializable
data class AbstractSubClass(override val foo: Int, val bar: String) : AbstractClass()
