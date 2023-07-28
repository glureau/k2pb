package com.glureau.k2pb.sample.lib

import com.glureau.sample.lib.DataClassFromLib
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.Test

@OptIn(ExperimentalSerializationApi::class)
class EncodingTest {

    private val protoBuf: ProtoBuf = ProtoBuf {}

    @Test
    fun encodeFromSerialization() {
        val origin = DataClassFromLib(51)
        val encoded = protoBuf.encodeToByteArray(origin)
        val decoded = DataClassFromLibOuterClass.DataClassFromLib.parseFrom(encoded)
        assert(decoded.myInt == origin.myInt)
    }

    @Test
    fun encodeFromProtobuf() {
        val origin = DataClassFromLibOuterClass.DataClassFromLib.newBuilder()
            .setMyInt(42)
            .build()
        val encoded = origin.toByteArray()
        val decoded = protoBuf.decodeFromByteArray<DataClassFromLib>(encoded)
        assert(decoded.myInt == origin.myInt)
    }
}
