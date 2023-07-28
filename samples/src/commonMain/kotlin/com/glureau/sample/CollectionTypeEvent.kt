package com.glureau.sample

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoPacked

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CollectionTypeEvent(
    @ProtoPacked
    val integerList: List<Int> = listOf(42, 51),
    val mapStringInt: Map<String, Int> = mapOf(
        "a" to 1,
        "b" to 2,
    ),
) : EventInterface
