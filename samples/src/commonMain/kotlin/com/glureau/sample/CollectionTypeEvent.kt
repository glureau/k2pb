package com.glureau.sample

import kotlinx.serialization.Serializable

@Serializable
data class CollectionTypeEvent(
    val integerList: List<Int> = listOf(42, 51),
    val mapStringInt: Map<String, Int> = mapOf(
        "a" to 1,
        "b" to 2,
    ),
) : EventInterface
