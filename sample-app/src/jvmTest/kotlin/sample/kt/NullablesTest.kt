package sample.kt

import NullableBigDecimalValueClassHolderOuterClass
import NullableNativeTypeEventOuterClass
import NullableValueClassHolderOuterClass
import com.glureau.sample.NullableBigDecimalValueClass
import com.glureau.sample.NullableBigDecimalValueClassHolder
import com.glureau.sample.NullableNativeTypeEvent
import com.glureau.sample.NullableValueClassHolder
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
            protocInstance = NullableNativeTypeEventOuterClass.NullableNativeTypeEvent.newBuilder()
                .setInteger(42)
                .setLong(84L)
                .setFloat(12.34f)
                .setDouble(56.789)
                .setString("Hello World")
                .setShort(5342)
                .setChar('G'.toInt())
                .setBoolean(true)
                .setByte(42)
                .setByteArray("Hello World".toByteStringUtf8())
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
            protocInstance = NullableNativeTypeEventOuterClass.NullableNativeTypeEvent.newBuilder()
                .setIsIntegerNull(true)
                .setIsLongNull(true)
                .setIsFloatNull(true)
                .setIsDoubleNull(true)
                .setIsStringNull(true)
                .setIsShortNull(true)
                .setIsCharNull(true)
                .setIsBooleanNull(true)
                .setIsByteNull(true)
                .setIsByteArrayNull(true)
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
            protocInstance = NullableNativeTypeEventOuterClass.NullableNativeTypeEvent.newBuilder()
                .setIsIntegerNull(true)
                .setIsLongNull(true)
                .setIsFloatNull(true)
                .setIsDoubleNull(true)
                .setString("Hola !")
                .setIsShortNull(true)
                .setIsCharNull(true)
                .setIsBooleanNull(true)
                .setIsByteNull(true)
                .setIsByteArrayNull(true)
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
            protocInstance = NullableNativeTypeEventOuterClass.NullableNativeTypeEvent.newBuilder()
                .setInteger(0)
                .setLong(0L)
                .setFloat(0f)
                .setDouble(0.0)
                .setString("")
                .setShort(0)
                .setChar(0)
                .setBoolean(false)
                .setByte(0)
                .setByteArray("".toByteStringUtf8())
                .build()
        )
    }


    @Test
    fun nullableValueClassHolder_withData() {
        assertCompatibleSerialization(
            ktInstance = NullableValueClassHolder(ValueClassFromLib("42")),
            protocInstance = NullableValueClassHolderOuterClass.NullableValueClassHolder.newBuilder()
                .setValueClassFromLib("42")
                .build()
        )
    }

    @Test
    fun nullableValueClassHolder_withNull() {
        assertCompatibleSerialization(
            ktInstance = NullableValueClassHolder(null),
            protocInstance = NullableValueClassHolderOuterClass.NullableValueClassHolder.newBuilder()
                //.setValueClassFromLib("") // not required as it's protobuf default value
                .setIsValueClassFromLibNull(true)
                .build()
        )
    }

    @Test
    fun nullableValueClassHolder_withDefaults() {
        assertCompatibleSerialization(
            ktInstance = NullableValueClassHolder(ValueClassFromLib("")),
            protocInstance = NullableValueClassHolderOuterClass.NullableValueClassHolder.newBuilder()
                .setValueClassFromLib("")
                //.setIsValueClassFromLibNull(false) // not required as it's protobuf default value
                .build()
        )
    }

    @Test
    fun nullableBigDecimalValueClassHolder_withData() {
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalValueClassHolder(NullableBigDecimalValueClass(BigDecimal("42.42"))),
            protocInstance = NullableBigDecimalValueClassHolderOuterClass.NullableBigDecimalValueClassHolder.newBuilder()
                .setNullableBdValue("42.42")
                // .setIsNullableBdValueNull(false) // not required as it's protobuf default value
                .build()
        )
    }

    @Test
    fun nullableBigDecimalValueClassHolder_withNull() {
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalValueClassHolder(NullableBigDecimalValueClass(null)),
            protocInstance = NullableBigDecimalValueClassHolderOuterClass.NullableBigDecimalValueClassHolder.newBuilder()
                //.setNullableBdValue(null) // <- NPE in protoc generated java code, but default Java is null anyway
                .setIsNullableBdValueNull(true)
                .build()
        )
    }
}
