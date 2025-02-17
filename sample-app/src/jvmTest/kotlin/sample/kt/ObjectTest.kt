package sample.kt

import com.glureau.k2pb_sample.ObjectClassProto
import com.glureau.sample.ObjectClass
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class ObjectTest : BaseEncodingTest() {

    @Test
    fun checkObjectClass() {
        assertCompatibleSerialization(
            ktInstance = ObjectClass,
            protocInstance = ObjectClassProto.ObjectClass.newBuilder().build(),
        )
    }
}