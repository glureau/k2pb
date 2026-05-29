package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.NullableValueClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import com.glureau.sample.lib.ValueClassOfEnum
import com.glureau.sample.lib.ValueClassOfNullableEnum

@ProtoMessage
data class IntCollections(
    val integerList: List<Int> = emptyList(),
    val integerSet: Set<Int> = emptySet(),
    val nullableIntegerList: List<Int>? = null,
    val nullableIntegerSet: Set<Int>? = null,
) : EventInterface

@ProtoMessage
data class StringCollections(
    val stringList: List<String> = emptyList(),
    val stringSet: Set<String> = emptySet(),
    val nullableStringList: List<String>? = null,
    val nullableStringSet: Set<String>? = null,
) : EventInterface

@ProtoMessage
data class DataClassCollections(
    val dataClassList: List<DataClassFromLib> = emptyList(),
    val dataClassSet: Set<DataClassFromLib> = emptySet(),
    val mapStringInt: Map<String, Int> = emptyMap(),
    val mapStringObject: Map<String, DataClassFromLib> = emptyMap(),
) : EventInterface


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

@ProtoMessage
data class NullableCollection(
    val mapStringObject: Map<String, DataClassFromLib?>,
    // TODO:
) : EventInterface

@ProtoMessage
data class BooleanCollections(
    val booleanList: List<Boolean> = emptyList(),
    val booleanSet: Set<Boolean> = emptySet(),
    val nullableBooleanList: List<Boolean>? = null,
    val nullableBooleanSet: Set<Boolean>? = null,
) : EventInterface

@ProtoMessage
data class LongCollections(
    val longList: List<Long> = emptyList(),
    val longSet: Set<Long> = emptySet(),
    val nullableLongList: List<Long>? = null,
    val nullableLongSet: Set<Long>? = null,
) : EventInterface

@ProtoMessage
data class FloatCollections(
    val floatList: List<Float> = emptyList(),
    val floatSet: Set<Float> = emptySet(),
    val nullableFloatList: List<Float>? = null,
    val nullableFloatSet: Set<Float>? = null,
) : EventInterface

@ProtoMessage
data class DoubleCollections(
    val doubleList: List<Double> = emptyList(),
    val doubleSet: Set<Double> = emptySet(),
    val nullableDoubleList: List<Double>? = null,
    val nullableDoubleSet: Set<Double>? = null,
) : EventInterface

@ProtoMessage
data class CharCollections(
    val charList: List<Char> = emptyList(),
    val charSet: Set<Char> = emptySet(),
    val nullableCharList: List<Char>? = null,
    val nullableCharSet: Set<Char>? = null,
) : EventInterface

@ProtoMessage
data class ShortCollections(
    val shortList: List<Short> = emptyList(),
    val shortSet: Set<Short> = emptySet(),
    val nullableShortList: List<Short>? = null,
    val nullableShortSet: Set<Short>? = null,
) : EventInterface

@ProtoMessage
data class ByteCollections(
    val byteList: List<Byte> = emptyList(),
    val byteSet: Set<Byte> = emptySet(),
    val nullableByteList: List<Byte>? = null,
    val nullableByteSet: Set<Byte>? = null,
) : EventInterface
