package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage

@ProtoMessage
data class TransientField(
    val fieldSerialized: String,
    @Transient
    val fieldTransient: String = "default value",
)