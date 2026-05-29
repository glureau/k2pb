package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoField
import com.glureau.k2pb.annotation.ProtoMessage

@ProtoMessage
enum class EnumClass {
    A,
    B,
    @ProtoField("Colibri")
    C,
}

@ProtoMessage
enum class NonContiguousEnum {
    UNKNOWN,
    @ProtoField(number = 5)
    ACTIVE,
    @ProtoField(number = 10)
    INACTIVE,
}

@ProtoMessage
data class NonContiguousEnumHolder(
    val status: NonContiguousEnum = NonContiguousEnum.UNKNOWN,
)