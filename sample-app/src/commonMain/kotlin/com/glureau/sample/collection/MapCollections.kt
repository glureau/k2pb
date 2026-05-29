package com.glureau.sample.collection

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.EventInterface
import com.glureau.sample.lib.DataClassFromLib

@ProtoMessage
data class MapCollections(
    val mapStringInt: Map<String, Int> = emptyMap(),
    val mapStringObject: Map<String, DataClassFromLib> = emptyMap(),
    val mapStringBoolean: Map<String, Boolean> = emptyMap(),
) : EventInterface
