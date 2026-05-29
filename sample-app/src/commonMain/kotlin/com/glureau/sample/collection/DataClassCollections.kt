package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface
import com.glureau.sample.lib.DataClassFromLib

@ProtoMessage
data class DataClassCollections(
    val dataClassList: List<DataClassFromLib> = emptyList(),
    val dataClassSet: Set<DataClassFromLib> = emptySet(),
) : EventInterface

@ProtoMessage
data class NullableDataClassCollections(
    val nullableDataClassList: List<DataClassFromLib>? = null,
    val nullableDataClassSet: Set<DataClassFromLib>? = null,
) : EventInterface
