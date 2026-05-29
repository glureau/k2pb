package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface

@ProtoMessage
data class LongCollections(
    val longList: List<Long> = emptyList(),
    val longSet: Set<Long> = emptySet(),
) : EventInterface

@ProtoMessage
data class NullableLongCollections(
    val nullableLongList: List<Long>? = null,
    val nullableLongSet: Set<Long>? = null,
) : EventInterface
