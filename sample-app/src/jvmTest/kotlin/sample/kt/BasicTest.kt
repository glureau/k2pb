package sample.kt

import com.glureau.k2pb_sample.BarEventProto
import com.glureau.k2pb_sample.CommonClassProto
import com.glureau.k2pb_sample.FooEventProto
import com.glureau.k2pb_sample.StandardClassProto
import com.glureau.sample.BarEvent
import com.glureau.sample.CommonClass
import com.glureau.sample.FooEvent
import com.glureau.sample.StandardClass
import com.google.protobuf.kotlin.toByteString
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class BasicTest : BaseEncodingTest() {
    @Test
    fun dataClassBar() {
        assertCompatibleSerialization(
            ktInstance = BarEvent(CommonClass("helloworld")),
            protocInstance = BarEventProto.BarEvent.newBuilder()
                .setCommon(
                    CommonClassProto.CommonClass.newBuilder()
                        .setId("helloworld")
                        .build()
                )
                .build()
        )
    }

    @Test
    fun dataClassFoo() {
        assertCompatibleSerialization(
            ktInstance = FooEvent(CommonClass("helloworld")),
            protocInstance = FooEventProto.FooEvent.newBuilder()
                .setCommon(
                    CommonClassProto.CommonClass.newBuilder()
                        .setId("helloworld")
                        .build()
                )
                .build()
        )
    }

    @Test
    fun dataClassFooWithDefaultValues() {
        assertCompatibleSerialization(
            ktInstance = FooEvent(CommonClass("")),
            protocInstance = FooEventProto.FooEvent.newBuilder()
                .setCommon(
                    CommonClassProto.CommonClass.newBuilder()
                        .setId("")
                        .build()
                )
                .build()
        )
    }

    @Test
    fun checkStandardClass() {
        assertCompatibleSerialization(
            ktInstance = StandardClass("000-00", "helloworld".toByteArray()),
            protocInstance = StandardClassProto.StandardClass.newBuilder()
                .setEventUUID("000-00")
                .setBytes("helloworld".toByteArray().toByteString())
                .build()
        )
    }
    @Test
    fun checkStandardClassWithDefaultValues() {
        assertCompatibleSerialization(
            ktInstance = StandardClass("", "".toByteArray()),
            protocInstance = StandardClassProto.StandardClass.newBuilder()
                .setEventUUID("")
                .setBytes("".toByteArray().toByteString())
                .build()
        )
    }
}