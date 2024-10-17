package com.glureau.sample.lib

import com.glureau.k2pb.annotation.ProtoMessage
import kotlinx.serialization.Serializable

@Serializable
@ProtoMessage
data class DataClassFromLib(val myInt: Int)
