package sample.kt

import BigDecimalHolderOuterClass
import NullableBigDecimalHolderOuterClass
import com.glureau.sample.BigDecimalHolder
import com.glureau.sample.NullableBigDecimalHolder
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class CustomSerializerTest : BaseEncodingTest() {

    @Test
    fun checkCustomSerializer() {
        assertCompatibleSerialization(
            ktInstance = BigDecimalHolder(BigDecimal.parseString("42.42")),
            protocInstance = BigDecimalHolderOuterClass.BigDecimalHolder.newBuilder()
                .setBd("42.42")
                .build()
        )
    }

    @Test
    fun defaultValues() {
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalHolder(BigDecimal.parseString("0.0")),
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