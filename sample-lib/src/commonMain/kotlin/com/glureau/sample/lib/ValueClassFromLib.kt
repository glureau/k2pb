package com.glureau.sample.lib

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class ValueClassFromLib(val myValueIsString: String)
