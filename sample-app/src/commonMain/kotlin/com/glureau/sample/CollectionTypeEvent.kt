package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.lib.DataClassFromLib

@ProtoMessage
data class CollectionTypeEvent(
    val integerList: List<Int>,
    val stringList: List<String>,
    val maybeIntegerList: List<Int>?,
    val mapStringInt: Map<String, Int>,
    val dataClassList: List<DataClassFromLib>,
) : EventInterface
