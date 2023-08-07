package com.glureau.sample

import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import kotlinx.serialization.Serializable


@Serializable
data class MultiModule(
    val dataClassFromLib: DataClassFromLib,
    val valueClassFromLib: ValueClassFromLib,
)