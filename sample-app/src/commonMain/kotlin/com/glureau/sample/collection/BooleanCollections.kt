package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface

@ProtoMessage
data class BooleanCollections(
    val booleanList: List<Boolean> = emptyList(),
    val booleanSet: Set<Boolean> = emptySet(),
) : EventInterface

@ProtoMessage
data class NullableBooleanCollections(
    val nullableBooleanList: List<Boolean>? = null,
    val nullableBooleanSet: Set<Boolean>? = null,
) : EventInterface
