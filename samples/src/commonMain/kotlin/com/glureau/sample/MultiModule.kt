package com.glureau.sample

import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import kotlinx.serialization.Serializable

@Serializable
data class MultiModule(
    val todo: String = "issue with KSP to resolve types from another module, it should work though..."
    //val dataClassFromLib: DataClassFromLib,
    //val valueClassFromLib: ValueClassFromLib,
)