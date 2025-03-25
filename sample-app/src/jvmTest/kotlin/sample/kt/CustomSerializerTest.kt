package sample.kt

import com.glureau.k2pb.K2PBConstants.ExplicitNullability
import com.glureau.k2pb_sample.BigDecimalHolderProto
import com.glureau.k2pb_sample.NullableBigDecimalHolderProto
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
            protocInstance = BigDecimalHolderProto.BigDecimalHolder.newBuilder()
                .setBd("42.42")
                .build()
        )
    }

    @Test
    fun defaultValues() {
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalHolder(BigDecimal("0.0")),
            protocInstance = NullableBigDecimalHolderProto.NullableBigDecimalHolder.newBuilder()
                .setBd("0.0")
                .setIsBdNull(ExplicitNullability.NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullValues() {
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalHolder(null),
            protocInstance = NullableBigDecimalHolderProto.NullableBigDecimalHolder.newBuilder()
                //.setBd(null) // <- NPE in protoc generated java code, but default Java is null anyway
                .setIsBdNull(ExplicitNullability.NULL)
                .build()
        )
    }
}