package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface
import com.glureau.sample.lib.NullableValueClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import com.glureau.sample.lib.ValueClassOfEnum
import com.glureau.sample.lib.ValueClassOfNullableEnum

@ProtoMessage
data class InlinedCollection(
    val valueClassList: List<ValueClassFromLib>,
    val valueClassOfEnumList: List<ValueClassOfEnum>,
    val valueClassOfNullableEnumList: List<ValueClassOfNullableEnum>,
    val valueClassOfNullableStringList: List<NullableValueClassFromLib>,
    val valueClassSet: Set<ValueClassFromLib>,
    val valueClassOfEnumSet: Set<ValueClassOfEnum>,
    val valueClassOfNullableEnumSet: Set<ValueClassOfNullableEnum>,
    val valueClassOfNullableStringSet: Set<NullableValueClassFromLib>,
) : EventInterface

@ProtoMessage
data class NullableInlinedCollection(
    val valueClassList: List<ValueClassFromLib?>? = null,
    val valueClassOfEnumList: List<ValueClassOfEnum?>? = null,
    val valueClassOfNullableEnumList: List<ValueClassOfNullableEnum?>? = null,
    val valueClassOfNullableStringList: List<NullableValueClassFromLib?>? = null,
    val valueClassSet: Set<ValueClassFromLib?>? = null,
    val valueClassOfEnumSet: Set<ValueClassOfEnum?>? = null,
    val valueClassOfNullableEnumSet: Set<ValueClassOfNullableEnum?>? = null,
    val valueClassOfNullableStringSet: Set<NullableValueClassFromLib?>? = null,
) : EventInterface
