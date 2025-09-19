package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage


@ProtoMessage
data class BeforeAddingField(
    val a: String,
)

@ProtoMessage
data class AfterAddingField(
    val a: String,
    val b: Int,
)