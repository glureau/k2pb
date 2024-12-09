package sample.kt

import NativeTypeEventOuterClass
import com.glureau.sample.NativeTypeEvent
import com.google.protobuf.kotlin.toByteStringUtf8
import sample.kt.tools.BaseEncodingTest
import kotlin.test.Test

class NativeTypeTest : BaseEncodingTest() {
    @Test
    fun data() {
        assertCompatibleSerialization(
            ktInstance = NativeTypeEvent(
                integer = 42, // int32
                long = 84L, // int64
                float = 12.34f, // float
                double = 56.789, // float
                string = "Hello World", // string
                short = 5342, // int32
                char = 'G', // int32
                boolean = true, // bool
                byte = 42.toByte(), // int32
                byteArray = "Hello World".toByteArray(),
            ),
            protocInstance = NativeTypeEventOuterClass.NativeTypeEvent.newBuilder()
                .setInteger(42)
                .setLong(84L)
                .setFloat(12.34f)
                .setDouble(56.789)
                .setString("Hello World")
                .setShort(5342)
                .setChar('G'.toInt())
                .setBoolean(true)
                .setByte(42)
                .setByteArray("Hello World".toByteStringUtf8())
                .build()
        )
    }

    @Test
    fun defaults() {
        assertCompatibleSerialization(
            ktInstance = NativeTypeEvent(
                integer = 0, // int32
                long = 0L, // int64
                float = 0f, // float
                double = 0.0, // float
                string = "", // string
                short = 0.toShort(), // int32
                char = 0.toChar(), // int32
                boolean = false, // bool
                byte = 0.toByte(), // int32
                byteArray = "".toByteArray(),
            ),
            protocInstance = NativeTypeEventOuterClass.NativeTypeEvent.newBuilder()
                .setInteger(0)
                .setLong(0L)
                .setFloat(0f)
                .setDouble(0.0)
                .setString("")
                .setShort(0)
                .setChar(0)
                .setBoolean(false)
                .setByte(0)
                .setByteArray("".toByteStringUtf8())
                .build()
        )
    }
}