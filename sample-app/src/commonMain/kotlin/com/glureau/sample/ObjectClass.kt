package com.glureau.sample

import kotlinx.serialization.Serializable

@Serializable
object ObjectClass {
    val foo: Int = 42 // Not serializable
}