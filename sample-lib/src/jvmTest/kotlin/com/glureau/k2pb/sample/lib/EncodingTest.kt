package com.glureau.k2pb.sample.lib

import com.glureau.k2pb.runtime.K2PB
import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.k2pb_sample.DataClassFromLibOuterClass
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.registerSampleLibSerializers
import org.junit.Test


@OptIn(ExperimentalStdlibApi::class)
class EncodingTest {

    private val k2PB = K2PB {
        // Record sampleLibModule
        registerSampleLibSerializers()
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
}
