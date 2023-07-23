package com.glureau.sample

import kotlinx.serialization.Serializable


@Serializable
data class FooEvent(
    val common: CommonClass = CommonClass("id"),
)

@Serializable
data class BarEvent(
    val common: CommonClass = CommonClass("id"),
)

// Ensure this class is NOT duplicated in the generated code.
@Serializable
data class CommonClass(
    val id: String
) : EventInterface