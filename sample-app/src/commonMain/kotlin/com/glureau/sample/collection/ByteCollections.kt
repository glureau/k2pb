package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface

@ProtoMessage
data class ByteCollections(
    val byteList: List<Byte> = emptyList(),
    val byteSet: Set<Byte> = emptySet(),
) : EventInterface

@ProtoMessage
data class NullableByteCollections(
    val nullableByteList: List<Byte>? = null,
    val nullableByteSet: Set<Byte>? = null,
) : EventInterface
