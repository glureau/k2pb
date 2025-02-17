package sample.kt

import com.glureau.k2pb_sample.BarEventOuterClass
import com.glureau.k2pb_sample.CommonClassOuterClass
import com.glureau.k2pb_sample.FooEventOuterClass
import com.glureau.k2pb_sample.StandardClassOuterClass
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
            protocInstance = BarEventOuterClass.BarEvent.newBuilder()
                .setCommon(
                    CommonClassOuterClass.CommonClass.newBuilder()
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
            protocInstance = FooEventOuterClass.FooEvent.newBuilder()
                .setCommon(
                    CommonClassOuterClass.CommonClass.newBuilder()
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
            protocInstance = FooEventOuterClass.FooEvent.newBuilder()
                .setCommon(
                    CommonClassOuterClass.CommonClass.newBuilder()
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
            protocInstance = StandardClassOuterClass.StandardClass.newBuilder()
                .setEventUUID("000-00")
                .setBytes("helloworld".toByteArray().toByteString())
                .build()
        )
    }
    @Test
    fun checkStandardClassWithDefaultValues() {
        assertCompatibleSerialization(
            ktInstance = StandardClass("", "".toByteArray()),
            protocInstance = StandardClassOuterClass.StandardClass.newBuilder()
                .setEventUUID("")
                .setBytes("".toByteArray().toByteString())
                .build()
        )
    }
}