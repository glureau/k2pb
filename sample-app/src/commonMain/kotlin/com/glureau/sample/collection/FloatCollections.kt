package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface

@ProtoMessage
data class FloatCollections(
    val floatList: List<Float> = emptyList(),
    val floatSet: Set<Float> = emptySet(),
) : EventInterface

@ProtoMessage
data class NullableFloatCollections(
    val nullableFloatList: List<Float>? = null,
    val nullableFloatSet: Set<Float>? = null,
) : EventInterface
