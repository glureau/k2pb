package sample.kt

import BigDecimalHolderOuterClass
import NullableBigDecimalHolderOuterClass
import com.glureau.sample.BigDecimalHolder
import com.glureau.sample.NullableBigDecimalHolder
import org.junit.Test
import sample.kt.tools.BaseEncodingTest
import java.math.BigDecimal

class CustomSerializerTest : BaseEncodingTest() {

    @Test
    fun checkCustomSerializer() {
        assertCompatibleSerialization(
            ktInstance = BigDecimalHolder(BigDecimal("42.42")),
            protocInstance = BigDecimalHolderOuterClass.BigDecimalHolder.newBuilder()
                .setBd("42.42")
                .build()
        )
    }

    @Test
    fun defaultValues() {
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalHolder(BigDecimal("0.0")),
            protocInstance = NullableBigDecimalHolderOuterClass.NullableBigDecimalHolder.newBuilder()
                .setBd("0.0")
                .build()
        )
    }

    @Test
    fun nullValues() {
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalHolder(null),
            protocInstance = NullableBigDecimalHolderOuterClass.NullableBigDecimalHolder.newBuilder()
                //.setBd(null) // <- NPE in protoc generated java code, but default Java is null anyway
                .setIsBdNull(true)
                .build()
        )
    }
}