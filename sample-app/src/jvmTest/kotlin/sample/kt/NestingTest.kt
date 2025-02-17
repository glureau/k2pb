package sample.kt

import com.glureau.k2pb_sample.WithNestClassAProto
import com.glureau.sample.WithNestClassA
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class NestingTest : BaseEncodingTest() {

    @Test
    fun checkNestedClass() {
        assertCompatibleSerialization(
            ktInstance = WithNestClassA(WithNestClassA.NestedClass("helloworld")),
            protocInstance = WithNestClassAProto.WithNestClassA.newBuilder()
                .setA(
                    WithNestClassAProto.WithNestClassA.NestedClass.newBuilder()
                        .setNested("helloworld")
                        .build()
                )
                .build()
        )
    }
}