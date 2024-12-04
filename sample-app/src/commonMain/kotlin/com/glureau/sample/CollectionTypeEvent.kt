package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.lib.DataClassFromLib

@ProtoMessage
data class CollectionTypeEvent(
    val integerList: List<Int> = listOf(42, 51),
    val maybeIntegerList: List<Int>? = listOf(42, 51),
    val mapStringInt: Map<String, Int> = mapOf(
        "a" to 1,
        "b" to 2,
    ),
    val dataClassList: List<DataClassFromLib>,
) : EventInterface
