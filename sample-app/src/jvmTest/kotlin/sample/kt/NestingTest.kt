package sample.kt

import WithNestClassAOuterClass
import com.glureau.sample.WithNestClassA
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class NestingTest : BaseEncodingTest() {

    @Test
    fun checkNestedClass() {
        assertCompatibleSerialization(
            ktInstance = WithNestClassA(WithNestClassA.NestedClass("helloworld")),
            protocInstance = WithNestClassAOuterClass.WithNestClassA.newBuilder()
                .setA(
                    WithNestClassAOuterClass.WithNestClassA.NestedClass.newBuilder()
                        .setNested("helloworld")
                        .build()
                )
                .build()
        )
    }
}