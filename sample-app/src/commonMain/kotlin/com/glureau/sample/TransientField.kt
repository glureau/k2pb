package com.glureau.sample

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class TransientField(
    val fieldSerialized: String,
    @Transient
    val fieldTransient: String = "default value",
)
