package com.glureau.k2pb.sample.lib

import DataClassFromLibOuterClass
import com.glureau.k2pb.runtime.K2PB
import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.registerSampleLibSerializers
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.Test


@OptIn(ExperimentalStdlibApi::class)
class EncodingTest {

    private val protoBuf: ProtoBuf = ProtoBuf {}
    private val k2PB = K2PB {
        // Record sampleLibModule
        registerSampleLibSerializers()
    }

    @Test
    fun encodeFromSerialization() {
        val origin = DataClassFromLib(51)
        val encoded = protoBuf.encodeToByteArray(origin)
        val decoded = DataClassFromLibOuterClass.DataClassFromLib.parseFrom(encoded)
        assert(decoded.myInt == origin.myInt)
    }

    @Test
    fun encodeFromOurs() {
        val origin = DataClassFromLib(42)
        val encoded = k2PB.encodeToByteArray(origin)
        val protocEncoded = DataClassFromLibOuterClass.DataClassFromLib.newBuilder()
            .setMyInt(42)
            .build()
            .toByteArray()

        println("ours: " + encoded.joinToString(" ") { it.toHexString() })
        println("protoc: " + protocEncoded.joinToString(" ") { it.toHexString() })

        //val decoded = DataClassFromLibOuterClass.DataClassFromLib.parseFrom(encoded)
        //assert(decoded.myInt == origin.myInt)
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
