package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.lib.DataClassFromLib

@ProtoMessage
data class NullableDataClassHolder(
    val dataClassFromLib: DataClassFromLib?,
)
