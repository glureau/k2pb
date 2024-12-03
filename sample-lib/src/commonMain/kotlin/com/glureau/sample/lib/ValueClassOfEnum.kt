package com.glureau.sample.lib

import com.glureau.k2pb.annotation.ProtoMessage

@ProtoMessage
@JvmInline
value class ValueClassOfEnum(val enum: AnEnum)

@ProtoMessage
@JvmInline
value class ValueClassOfNullableEnum(val enum: AnEnum?)

@ProtoMessage
enum class AnEnum {
    AnEnum_A, AnEnum_B, AnEnum_C
}

@ProtoMessage
data class EnumHolder(val value: AnEnum)

@ProtoMessage
data class ValueClassOfEnumHolder(val value: ValueClassOfEnum)
