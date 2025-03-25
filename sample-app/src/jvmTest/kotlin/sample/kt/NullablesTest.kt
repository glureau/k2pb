package sample.kt

import com.glureau.custom.javapackage.DataClassFromLibProto
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.NullableBigDecimalValueClassHolderProto
import com.glureau.k2pb_sample.NullableDataClassHolderProto
import com.glureau.k2pb_sample.NullableNativeTypeEventProto
import com.glureau.k2pb_sample.NullableValueClassHolderProto
import com.glureau.sample.NullableBigDecimalValueClass
import com.glureau.sample.NullableBigDecimalValueClassHolder
import com.glureau.sample.NullableDataClassHolder
import com.glureau.sample.NullableNativeTypeEvent
import com.glureau.sample.NullableValueClassHolder
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import com.google.protobuf.kotlin.toByteStringUtf8
import org.junit.Test
import sample.kt.tools.BaseEncodingTest
import java.math.BigDecimal

class NullablesTest : BaseEncodingTest() {

    @Test
    fun data() {
        assertCompatibleSerialization(
            ktInstance = NullableNativeTypeEvent(
                integer = 42, // int32
                long = 84L, // int64
                float = 12.34f, // float
                double = 56.789, // float
                string = "Hello World", // string
                short = 5342, // int32
                char = 'G', // int32
                boolean = true, // bool
                byte = 42.toByte(), // int32
                byteArray = "Hello World".toByteArray(),
            ),
            protocInstance = NullableNativeTypeEventProto.NullableNativeTypeEvent.newBuilder()
                .setInteger(42)
                .setIsIntegerNull(NOT_NULL)
                .setLong(84L)
                .setIsLongNull(NOT_NULL)
                .setFloat(12.34f)
                .setIsFloatNull(NOT_NULL)
                .setDouble(56.789)
                .setIsDoubleNull(NOT_NULL)
                .setString("Hello World")
                .setIsStringNull(NOT_NULL)
                .setShort(5342)
                .setIsShortNull(NOT_NULL)
                .setChar('G'.toInt())
                .setIsCharNull(NOT_NULL)
                .setBoolean(true)
                .setIsBooleanNull(NOT_NULL)
                .setByte(42)
                .setIsByteNull(NOT_NULL)
                .setByteArray("Hello World".toByteStringUtf8())
                .setIsByteArrayNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nulls() {
        assertCompatibleSerialization(
            ktInstance = NullableNativeTypeEvent(
                integer = null,
                long = null,
                float = null,
                double = null,
                string = null,
                short = null,
                char = null,
                boolean = null,
                byte = null,
                byteArray = null,
            ),
            protocInstance = NullableNativeTypeEventProto.NullableNativeTypeEvent.newBuilder()
                .setIsIntegerNull(NULL)
                .setIsLongNull(NULL)
                .setIsFloatNull(NULL)
                .setIsDoubleNull(NULL)
                .setIsStringNull(NULL)
                .setIsShortNull(NULL)
                .setIsCharNull(NULL)
                .setIsBooleanNull(NULL)
                .setIsByteNull(NULL)
                .setIsByteArrayNull(NULL)
                .build()
        )
    }

    @Test
    fun nullsButOneField() {
        assertCompatibleSerialization(
            ktInstance = NullableNativeTypeEvent(
                integer = null,
                long = null,
                float = null,
                double = null,
                string = "Hola !",
                short = null,
                char = null,
                boolean = null,
                byte = null,
                byteArray = null,
            ),
            protocInstance = NullableNativeTypeEventProto.NullableNativeTypeEvent.newBuilder()
                .setIsIntegerNull(NULL)
                .setIsLongNull(NULL)
                .setIsFloatNull(NULL)
                .setIsDoubleNull(NULL)
                .setString("Hola !")
                .setIsStringNull(NOT_NULL)
                .setIsShortNull(NULL)
                .setIsCharNull(NULL)
                .setIsBooleanNull(NULL)
                .setIsByteNull(NULL)
                .setIsByteArrayNull(NULL)
                .build()
        )
    }

    @Test
    fun defaults() {
        assertCompatibleSerialization(
            ktInstance = NullableNativeTypeEvent(
                integer = 0, // int32
                long = 0L, // int64
                float = 0f, // float
                double = 0.0, // float
                string = "", // string
                short = 0.toShort(), // int32
                char = 0.toChar(), // int32
                boolean = false, // bool
                byte = 0.toByte(), // int32
                byteArray = "".toByteArray(),
            ),
            protocInstance = NullableNativeTypeEventProto.NullableNativeTypeEvent.newBuilder()
                .setInteger(0)
                .setIsIntegerNull(NOT_NULL)
                .setLong(0L)
                .setIsLongNull(NOT_NULL)
                .setFloat(0f)
                .setIsFloatNull(NOT_NULL)
                .setDouble(0.0)
                .setIsDoubleNull(NOT_NULL)
                .setString("")
                .setIsStringNull(NOT_NULL)
                .setShort(0)
                .setIsShortNull(NOT_NULL)
                .setChar(0)
                .setIsCharNull(NOT_NULL)
                .setBoolean(false)
                .setIsBooleanNull(NOT_NULL)
                .setByte(0)
                .setIsByteNull(NOT_NULL)
                .setByteArray("".toByteStringUtf8())
                .setIsByteArrayNull(NOT_NULL)
                .build()
        )
    }


    @Test
    fun nullableValueClassHolder_withData() {
        assertCompatibleSerialization(
            ktInstance = NullableValueClassHolder(ValueClassFromLib("42")),
            protocInstance = NullableValueClassHolderProto.NullableValueClassHolder.newBuilder()
                .setValueClassFromLib("42")
                .setIsValueClassFromLibNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableValueClassHolder_withNull() {
        assertCompatibleSerialization(
            ktInstance = NullableValueClassHolder(null),
            protocInstance = NullableValueClassHolderProto.NullableValueClassHolder.newBuilder()
                .setIsValueClassFromLibNull(NULL)
                .build()
        )
    }

    @Test
    fun nullableValueClassHolder_withDefaults() {
        assertCompatibleSerialization(
            ktInstance = NullableValueClassHolder(ValueClassFromLib("")),
            protocInstance = NullableValueClassHolderProto.NullableValueClassHolder.newBuilder()
                .setValueClassFromLib("")
                .setIsValueClassFromLibNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableDataClassHolder_withNull() {
        assertCompatibleSerialization(
            ktInstance = NullableDataClassHolder(null),
            protocInstance = NullableDataClassHolderProto.NullableDataClassHolder.newBuilder()
                //.setIsDataClassFromLibNull(NULL) // No explicit nullability for message, null is default
                .build()
        )
    }

    @Test
    fun nullableDataClassHolder_withDefaults() {
        assertCompatibleSerialization(
            ktInstance = NullableDataClassHolder(DataClassFromLib(22)),
            protocInstance = NullableDataClassHolderProto.NullableDataClassHolder.newBuilder()
                .setDataClassFromLib(DataClassFromLibProto.DataClassFromLib.newBuilder().setMyInt(22).build())
                //.setIsDataClassFromLibNull(NOT_NULL) // No explicit nullability for message, null is default
                .build()
        )
    }

    @Test
    fun nullableBigDecimalValueClassHolder_withData() {
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalValueClassHolder(NullableBigDecimalValueClass(BigDecimal("42.42"))),
            protocInstance = NullableBigDecimalValueClassHolderProto.NullableBigDecimalValueClassHolder.newBuilder()
                .setNullableBdValue("42.42")
                .setIsNullableBdValueNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableBigDecimalValueClassHolder_withNull() {
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalValueClassHolder(NullableBigDecimalValueClass(null)),
            protocInstance = NullableBigDecimalValueClassHolderProto.NullableBigDecimalValueClassHolder.newBuilder()
                .setIsNullableBdValueNull(NULL)
                .build()
        )
    }
}
