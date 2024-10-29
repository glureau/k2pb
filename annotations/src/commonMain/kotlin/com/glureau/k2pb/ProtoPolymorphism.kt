package com.glureau.k2pb

import kotlin.reflect.KClass

@Repeatable
annotation class ProtoPolymorphism(
    val parent: KClass<*>,
    val oneOf: Array<Pair>,
    // We could define a boolean at true by default to check that children are inheriting from parent,
    // Without that the deserialization may fail to cast...
) {
    annotation class Pair(val kClass: KClass<*>, val number: Int)
}
