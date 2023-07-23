package com.glureau.sample

import kotlinx.serialization.Serializable

@Serializable
data class WithNestClassA(
    val a: NestedClass = NestedClass("nested A"),
) {
    @Serializable
    data class NestedClass(
        val nested: String = "nested",
    )
}

@Serializable
data class WithNestClassB(
    val b: NestedClass = NestedClass("nested B"),
) {
    @Serializable
    data class NestedClass(
        val nested: String = "nested",
    )
}


@Serializable
data class WithNestedEnum(
    val enum: NestedEnum = NestedEnum.C,
) {
    enum class NestedEnum {
        A, B, C
    }
}