package sample.kt

import ObjectClassOuterClass
import com.glureau.sample.ObjectClass
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class ObjectTest : BaseEncodingTest() {

    @Test
    fun checkObjectClass() {
        assertCompatibleSerialization(
            ktInstance = ObjectClass,
            protocInstance = ObjectClassOuterClass.ObjectClass.newBuilder().build(),
        )
    }
}