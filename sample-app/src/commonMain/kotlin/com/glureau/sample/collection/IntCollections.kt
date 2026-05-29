package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface

@ProtoMessage
data class IntCollections(
    val integerList: List<Int> = emptyList(),
    val integerSet: Set<Int> = emptySet(),
) : EventInterface

@ProtoMessage
data class NullableIntCollections(
    val nullableIntegerList: List<Int>? = null,
    val nullableIntegerSet: Set<Int>? = null,
) : EventInterface
