package com.glureau.sample

import com.glureau.sample.lib.DataClassFromLib
import kotlinx.serialization.Serializable

@Serializable
data class MultiModule(
    val dataClassFromLib: DataClassFromLib,
)
