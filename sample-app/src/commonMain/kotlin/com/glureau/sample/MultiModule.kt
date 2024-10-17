package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.ValueClassFromLib

@ProtoMessage
data class MultiModule(
    val dataClassFromLib: DataClassFromLib,
    val valueClassFromLib: ValueClassFromLib,
)