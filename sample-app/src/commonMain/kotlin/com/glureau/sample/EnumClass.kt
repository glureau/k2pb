package com.glureau.sample

import kotlinx.serialization.Serializable

// @Serializable is not required usually, but here the enum is not referenced in another serializable,
// and we want to trigger the generation.
@Serializable
enum class EnumClass {
    A,
    B,
    C,
}