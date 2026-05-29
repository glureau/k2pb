package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface

@ProtoMessage
data class CharCollections(
    val charList: List<Char> = emptyList(),
    val charSet: Set<Char> = emptySet(),
) : EventInterface

@ProtoMessage
data class NullableCharCollections(
    val nullableCharList: List<Char>? = null,
    val nullableCharSet: Set<Char>? = null,
) : EventInterface
