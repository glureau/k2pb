package com.glureau.sample

import kotlinx.serialization.Serializable

@Serializable
data class NativeTypeEvent(
    // Using same mapping than in Kotlinx Serialization:
    // https://github.com/Kotlin/kotlinx.serialization/blob/master/formats/protobuf/commonMain/src/kotlinx/serialization/protobuf/internal/ProtobufEncoding.kt
    val integer: Int = 42, // int32
    val long: Long = 84L, // int64
    val float: Float = 12.34f, // float
    val double: Double = 56.789, // float
    val string: String = "Hello World", // string
    val short: Short = 5342, // int32
    val char: Char = 'G', // int32
    val boolean: Boolean = true, // bool
    val byte: Byte = 42.toByte(), // int32
) : EventInterface
