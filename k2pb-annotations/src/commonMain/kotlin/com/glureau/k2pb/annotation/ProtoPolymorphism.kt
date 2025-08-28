package com.glureau.k2pb.annotation

import kotlin.reflect.KClass

@Repeatable
public annotation class ProtoPolymorphism(
    val parent: KClass<*>,
    val name: String,
    val deprecateOneOf: Array<DeprecatedField> = [],
    val oneOf: Array<Child>,
    // We could define a boolean at true by default to check that children are inheriting from parent,
    // Without that the deserialization may fail to cast...
) {
    public annotation class Child(
        val kClass: KClass<*>,
        val number: Int,
    )
}