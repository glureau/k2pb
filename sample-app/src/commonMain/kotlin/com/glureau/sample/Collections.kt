package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.NullableValueClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import com.glureau.sample.lib.ValueClassOfEnum
import com.glureau.sample.lib.ValueClassOfNullableEnum

@ProtoMessage
data class CollectionType(
    val integerList: List<Int>,
    val stringList: List<String>,
    val maybeIntegerList: List<Int>?,
    val mapStringInt: Map<String, Int>,
    val dataClassList: List<DataClassFromLib>,
    val mapStringObject: Map<String, DataClassFromLib>,
) : EventInterface


@ProtoMessage
data class InlinedCollection(
    val valueClassList: List<ValueClassFromLib>,
    val valueClassOfEnumList: List<ValueClassOfEnum>,
    val valueClassOfNullableEnumList: List<ValueClassOfNullableEnum>,
    val valueClassOfNullableStringList: List<NullableValueClassFromLib>,
) : EventInterface


@ProtoMessage
data class NullableInlinedCollection(
    val valueClassList: List<ValueClassFromLib?>? = null,
    val valueClassOfEnumList: List<ValueClassOfEnum?>? = null,
    val valueClassOfNullableEnumList: List<ValueClassOfNullableEnum?>? = null,
    val valueClassOfNullableStringList: List<NullableValueClassFromLib?>? = null,
) : EventInterface

@ProtoMessage
data class NullableCollection(
    val mapStringObject: Map<String, DataClassFromLib?>,
    // TODO:
) : EventInterface
