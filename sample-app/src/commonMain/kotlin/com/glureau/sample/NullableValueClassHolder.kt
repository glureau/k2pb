package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.lib.ValueClassFromLib

@ProtoMessage
data class NullableValueClassHolder(
    val valueClassFromLib: ValueClassFromLib?,
)
