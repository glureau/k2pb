package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface

@ProtoMessage
data class StringCollections(
    val stringList: List<String> = emptyList(),
    val stringSet: Set<String> = emptySet(),
) : EventInterface

@ProtoMessage
data class NullableStringCollections(
    val nullableStringList: List<String>? = null,
    val nullableStringSet: Set<String>? = null,
) : EventInterface
