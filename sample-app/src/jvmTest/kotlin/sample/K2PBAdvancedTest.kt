package sample

import com.glureau.k2pb.runtime.K2PB
import com.glureau.k2pb.runtime.decodeFromByteArray
import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.sample.*
import com.glureau.sample.lib.registerSampleLibSerializers
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals

class K2PBAdvancedTest {
    private val serializer = K2PB {
        registerSampleLibSerializers()
        registerSampleAppSerializers()
    }

    @Test
    fun `test BigDecimal serialization`() {
        val bigDecimal = BigDecimal("3.14159")
        val bigDecimalHolder = BigDecimalHolder(bigDecimal)
        val serialized = serializer.encodeToByteArray<BigDecimalHolder>(bigDecimalHolder)
        val deserialized = serializer.decodeFromByteArray<BigDecimalHolder>(serialized)
        assertEquals(bigDecimalHolder, deserialized)

        val nullableBigDecimalHolder = NullableBigDecimalHolder(bigDecimal)
        val serializedNullable = serializer.encodeToByteArray<NullableBigDecimalHolder>(nullableBigDecimalHolder)
        val deserializedNullable = serializer.decodeFromByteArray<NullableBigDecimalHolder>(serializedNullable)
        assertEquals(nullableBigDecimalHolder, deserializedNullable)
    }

    @Test
    fun `test BigDecimal value class serialization`() {
        val bigDecimal = BigDecimal("3.14159")
        val bigDecimalValueClass = BigDecimalValueClass(bigDecimal)
        val bigDecimalValueClassHolder = BigDecimalValueClassHolder(bigDecimalValueClass)
        val serialized = serializer.encodeToByteArray<BigDecimalValueClassHolder>(bigDecimalValueClassHolder)
        val deserialized = serializer.decodeFromByteArray<BigDecimalValueClassHolder>(serialized)
        assertEquals(bigDecimalValueClassHolder, deserialized)

        val nullableBigDecimalValueClass = NullableBigDecimalValueClass(bigDecimal)
        val nullableBigDecimalValueClassHolder = NullableBigDecimalValueClassHolder(nullableBigDecimalValueClass)
        val serializedNullable =
            serializer.encodeToByteArray<NullableBigDecimalValueClassHolder>(nullableBigDecimalValueClassHolder)
        val deserializedNullable =
            serializer.decodeFromByteArray<NullableBigDecimalValueClassHolder>(serializedNullable)
        assertEquals(nullableBigDecimalValueClassHolder, deserializedNullable)
    }

    @Test
    fun `test nullable enum holder serialization`() {
        val nullableEnumHolderNull = NullableEnumHolderUnspecifiedNull(com.glureau.sample.lib.AnEnum.AnEnum_A)
        val serializedNull = serializer.encodeToByteArray<NullableEnumHolderUnspecifiedNull>(nullableEnumHolderNull)
        val deserializedNull = serializer.decodeFromByteArray<NullableEnumHolderUnspecifiedNull>(serializedNull)
        assertEquals(nullableEnumHolderNull, deserializedNull)

        val nullableEnumHolderDefault = NullableEnumHolderUnspecifiedDefault(com.glureau.sample.lib.AnEnum.AnEnum_B)
        val serializedDefault =
            serializer.encodeToByteArray<NullableEnumHolderUnspecifiedDefault>(nullableEnumHolderDefault)
        val deserializedDefault =
            serializer.decodeFromByteArray<NullableEnumHolderUnspecifiedDefault>(serializedDefault)
        assertEquals(nullableEnumHolderDefault, deserializedDefault)
    }

    @Test
    fun `test nullable native type event serialization`() {
        val nullableNativeTypeEventNull = NullableNativeTypeEventUnspecifiedNull(
            integer = 42,
            long = 123456789L,
            float = 3.14f,
            double = 3.14159,
            string = "test",
            short = 123,
            char = 'A',
            boolean = true,
            byte = 127,
            byteArray = byteArrayOf(1, 2, 3)
        )
        val serializedNull =
            serializer.encodeToByteArray<NullableNativeTypeEventUnspecifiedNull>(nullableNativeTypeEventNull)
        val deserializedNull = serializer.decodeFromByteArray<NullableNativeTypeEventUnspecifiedNull>(serializedNull)
        assertEquals(nullableNativeTypeEventNull, deserializedNull)

        val nullableNativeTypeEventDefault = NullableNativeTypeEventUnspecifiedDefault(
            integer = 42,
            long = 123456789L,
            float = 3.14f,
            double = 3.14159,
            string = "test",
            short = 123,
            char = 'A',
            boolean = true,
            byte = 127,
            byteArray = byteArrayOf(1, 2, 3)
        )
        val serializedDefault =
            serializer.encodeToByteArray<NullableNativeTypeEventUnspecifiedDefault>(nullableNativeTypeEventDefault)
        val deserializedDefault =
            serializer.decodeFromByteArray<NullableNativeTypeEventUnspecifiedDefault>(serializedDefault)
        assertEquals(nullableNativeTypeEventDefault, deserializedDefault)
    }

    @Test
    fun `test native type event serialization`() {
        val nativeTypeEvent = NativeTypeEventUnspecifiedDefault(
            integer = 42,
            long = 123456789L,
            float = 3.14f,
            double = 3.14159,
            string = "test",
            short = 123,
            char = 'A',
            boolean = true,
            byte = 127,
            byteArray = byteArrayOf(1, 2, 3)
        )
        val serialized = serializer.encodeToByteArray<NativeTypeEventUnspecifiedDefault>(nativeTypeEvent)
        val deserialized = serializer.decodeFromByteArray<NativeTypeEventUnspecifiedDefault>(serialized)
        assertEquals(nativeTypeEvent, deserialized)
    }
} 