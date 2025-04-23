package com.glureau.k2pb.annotation

import kotlin.reflect.KClass

@Repeatable
public annotation class ProtoPolymorphism(
    val parent: KClass<*>,
    val name: String,
    val deprecateOneOf: Array<Deprecated> = [],
    val oneOf: Array<Child>,
    // We could define a boolean at true by default to check that children are inheriting from parent,
    // Without that the deserialization may fail to cast...
) {
    /**
     * Deprecated annotation helps to support the removal of a Kotlin class while it's still declared in protobuf
     * files for retrocompatibility.
     */
    public annotation class Deprecated(
        /**
         * Proto name (the name previously declared in ProtoMessage) of the class.
         */
        val protoName: String,
        /**
         * The number of the field in the one of message.
         */
        val protoNumber: Int,
        /**
         * Reasons of the deprecation, this will be copied into the proto file documentation.
         */
        val deprecationReason: String = "",
        /**
         * If true, the [protoName] is still used in the proto file (with [deprecationReason] as comment).
         * If false, the [protoName] and [protoNumber] are marked as **reserved** values.
         * https://protobuf.dev/programming-guides/proto3/#reserved
         */
        val publishedInProto: Boolean = true,
    )

    public annotation class Child(
        val kClass: KClass<*>,
        val number: Int
    )
}