package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage


@ProtoMessage
data class FooEvent(
    val common: CommonClass = CommonClass("id"),
)

@ProtoMessage
data class BarEvent(
    val common: CommonClass = CommonClass("id"),
)

// Ensure this class is NOT duplicated in the generated code.
@ProtoMessage
data class CommonClass(
    val id: String
) : EventInterface