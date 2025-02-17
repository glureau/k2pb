package sample.kt

import com.glureau.k2pb_sample.TransientFieldOuterClass
import com.glureau.sample.TransientField
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class TransientTest : BaseEncodingTest() {

    @Test
    fun checkTransientField() {
        assertCompatibleSerialization(
            // Here the default value is required because the assertion is checking via 'data class' equals
            ktInstance = TransientField(fieldSerialized = "hello", fieldTransient = "default value"),
            protocInstance = TransientFieldOuterClass.TransientField.newBuilder().setFieldSerialized("hello").build(),
        )
    }

}