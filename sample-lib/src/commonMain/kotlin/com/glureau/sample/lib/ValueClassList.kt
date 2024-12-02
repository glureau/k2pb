package com.glureau.sample.lib

import com.glureau.k2pb.annotation.ProtoMessage

@ProtoMessage
data class ValueClassList(
    val valueClassFromLibs: List<ValueClassFromLib>,
)
