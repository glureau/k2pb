package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface

@ProtoMessage
data class DoubleCollections(
    val doubleList: List<Double> = emptyList(),
    val doubleSet: Set<Double> = emptySet(),
) : EventInterface

@ProtoMessage
data class NullableDoubleCollections(
    val nullableDoubleList: List<Double>? = null,
    val nullableDoubleSet: Set<Double>? = null,
) : EventInterface
