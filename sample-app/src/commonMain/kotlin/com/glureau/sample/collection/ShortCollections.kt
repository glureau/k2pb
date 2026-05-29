package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface

@ProtoMessage
data class ShortCollections(
    val shortList: List<Short> = emptyList(),
    val shortSet: Set<Short> = emptySet(),
) : EventInterface

@ProtoMessage
data class NullableShortCollections(
    val nullableShortList: List<Short>? = null,
    val nullableShortSet: Set<Short>? = null,
) : EventInterface
