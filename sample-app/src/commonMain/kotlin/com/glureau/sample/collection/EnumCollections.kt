package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface
import com.glureau.sample.lib.AnEnum

@ProtoMessage
data class EnumCollections(
    val enumList: List<AnEnum> = emptyList(),
    val enumSet: Set<AnEnum> = emptySet(),
) : EventInterface
