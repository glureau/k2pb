package sample

import AbstractSubClassOuterClass
import BarEventOuterClass
import BigDecimalHolderOuterClass
import BigDecimalValueClassHolderOuterClass
import CollectionTypeEventOuterClass
import CommentedClassOuterClass
import CommonClassOuterClass
import FooEventOuterClass
import NativeTypeEventOuterClass
import NullableBigDecimalHolderOuterClass
import NullableBigDecimalValueClassHolderOuterClass
import NullableNativeTypeEventOuterClass
import ObjectClassOuterClass
import StandardClassOuterClass
import TransientFieldOuterClass
import VehicleKt.bike
import VehicleKt.car
import WithNestClassAOuterClass
import abstractClass
import abstractSubClass
import com.glureau.sample.AbstractClass
import com.glureau.sample.AbstractSubClass
import com.glureau.sample.BarEvent
import com.glureau.sample.BigDecimalHolder
import com.glureau.sample.BigDecimalValueClass
import com.glureau.sample.BigDecimalValueClassHolder
import com.glureau.sample.CollectionTypeEvent
import com.glureau.sample.CommentedClass
import com.glureau.sample.CommonClass
import com.glureau.sample.FooEvent
import com.glureau.sample.MultiModule
import com.glureau.sample.NativeTypeEvent
import com.glureau.sample.NullableBigDecimalHolder
import com.glureau.sample.NullableBigDecimalValueClass
import com.glureau.sample.NullableBigDecimalValueClassHolder
import com.glureau.sample.NullableNativeTypeEvent
import com.glureau.sample.ObjectClass
import com.glureau.sample.StandardClass
import com.glureau.sample.TransientField
import com.glureau.sample.User
import com.glureau.sample.Vehicle
import com.glureau.sample.WithNestClassA
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import com.glureau.sample.lib.ValueClassList
import com.google.protobuf.kotlin.toByteString
import com.google.protobuf.kotlin.toByteStringUtf8
import dataClassFromLib
import multiModule
import org.junit.Test
import user
import valueClassList
import vehicle
import java.math.BigDecimal

class EncodingTest : BaseEncodingTest() {

    @Test
    fun checkCommonClass() {
        assertCompatibleSerialization(
            ktInstance = BarEvent(CommonClass("helloworld")),
            protocInstance = BarEventOuterClass.BarEvent.newBuilder()
                .setCommon(
                    CommonClassOuterClass.CommonClass.newBuilder()
                        .setId("helloworld")
                        .build()
                )
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = FooEvent(CommonClass("helloworld")),
            protocInstance = FooEventOuterClass.FooEvent.newBuilder()
                .setCommon(
                    CommonClassOuterClass.CommonClass.newBuilder()
                        .setId("helloworld")
                        .build()
                )
                .build()
        )
    }

    @Test
    fun checkNativeType() {
        assertCompatibleSerialization(
            ktInstance = NativeTypeEvent(
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
            protocInstance = NativeTypeEventOuterClass.NativeTypeEvent.newBuilder()
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
    fun checkNullableNativeType() {
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
    fun checkCollectionTypeEvent() {
        assertCompatibleSerialization(
            ktInstance = CollectionTypeEvent(
                integerList = listOf(1, 3, 5),
                mapStringInt = mapOf(
                    "a" to 2,
                    "b" to 4,
                ),
            ),
            protocInstance = CollectionTypeEventOuterClass.CollectionTypeEvent.newBuilder()
                .addIntegerList(1)
                .addIntegerList(3)
                .putMapStringInt("a", 2)
                .addIntegerList(5)
                .putMapStringInt("b", 4)
                .build()
        )
    }


    @Test
    fun checkCommentedClass() {
        assertCompatibleSerialization(
            ktInstance = CommentedClass("helloworld"),
            protocInstance = CommentedClassOuterClass.CommentedClass.newBuilder()
                .setFieldWithComment("helloworld")
                .build()
        )
    }

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

    @Test
    fun checkSealedClass() {
        assertCompatibleSerialization(
            ktInstance = User(name = "Tony", vehicle = Vehicle.Car("Tesla")),
            protocInstance = user {
                name = "Tony"
                vehicle = vehicle {
                    car = car {
                        brand = "Tesla"
                    }
                }
            }
        )

        assertCompatibleSerialization(
            ktInstance = User(name = "Francis", vehicle = Vehicle.Bike("Peugeot")),
            protocInstance = user {
                name = "Francis"
                vehicle = vehicle {
                    bike = bike {
                        brand = "Peugeot"
                    }
                }
            }
        )
    }


    @Test
    fun checkMultiModule() {
        assertCompatibleSerialization(
            ktInstance = MultiModule(DataClassFromLib(51), ValueClassFromLib("42")),
            protocInstance = multiModule {
                dataClassFromLib = dataClassFromLib {
                    myInt = 51
                }
                valueClassFromLib = "42"
            }
        )
    }

    @Test
    fun checkCustomSerializer() {
        assertCompatibleSerialization(
            ktInstance = BigDecimalHolder(BigDecimal("42.42")),
            protocInstance = BigDecimalHolderOuterClass.BigDecimalHolder.newBuilder()
                .setBd("42.42")
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalHolder(BigDecimal("42.42")),
            protocInstance = NullableBigDecimalHolderOuterClass.NullableBigDecimalHolder.newBuilder()
                .setBd("42.42")
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalHolder(null),
            protocInstance = NullableBigDecimalHolderOuterClass.NullableBigDecimalHolder.newBuilder()
                //.setBd(null) // <- NPE
                .build()
        )
    }

    @Test
    fun checkInlineCustomSerializer() {
        assertCompatibleSerialization(
            ktInstance = BigDecimalValueClassHolder(BigDecimalValueClass(BigDecimal("42.42"))),
            protocInstance = BigDecimalValueClassHolderOuterClass.BigDecimalValueClassHolder.newBuilder()
                .setBdValue("42.42")
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalValueClassHolder(NullableBigDecimalValueClass(BigDecimal("42.42"))),
            protocInstance = NullableBigDecimalValueClassHolderOuterClass.NullableBigDecimalValueClassHolder.newBuilder()
                .setNullableBdValue("42.42")
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = NullableBigDecimalValueClassHolder(NullableBigDecimalValueClass(null)),
            protocInstance = NullableBigDecimalValueClassHolderOuterClass.NullableBigDecimalValueClassHolder.newBuilder()
                //.setNullableBdValue(null) // <- NPE
                .build()
        )
    }

    @Test
    fun checkObjectClass() {
        assertCompatibleSerialization(
            ktInstance = ObjectClass,
            protocInstance = ObjectClassOuterClass.ObjectClass.newBuilder().build(),
        )
    }

    @Test
    fun checkTransientField() {
        assertCompatibleSerialization(
            // Here the default value is required because the assertion is checking via 'data class' equals
            ktInstance = TransientField(fieldSerialized = "hello", fieldTransient = "default value"),
            protocInstance = TransientFieldOuterClass.TransientField.newBuilder().setFieldSerialized("hello").build(),
        )
    }

    @Test
    fun checkAbstractSubClass() {
        assertCompatibleSerialization(
            ktInstance = AbstractSubClass(foo = 2, bar = "asc"),
            protocInstance = AbstractSubClassOuterClass.AbstractSubClass.newBuilder().setFoo(2).setBar("asc").build(),
        )
    }

    @Test
    fun checkAbstractClassPolymorphism() {
        // de/serialization is done via AbstractClass polymorphism
        assertCompatibleSerialization<AbstractClass>(
            ktInstance = AbstractSubClass(foo = 2, bar = "asc") as AbstractClass,
            protocInstance = abstractClass {
                abstractSubClass = abstractSubClass {
                    foo = 2
                    bar = "asc"
                }
            }
        )
    }

    @Test
    fun checkStandardClass() {
        assertCompatibleSerialization(
            ktInstance = StandardClass("000-00", "helloworld".toByteArray()),
            protocInstance = StandardClassOuterClass.StandardClass.newBuilder()
                .setEventUUID("000-00")
                .setBytes("helloworld".toByteArray().toByteString())
                //.setFoo("hello")
                .build()
        )
    }

    @Test
    fun listOfValueClass() {
        assertCompatibleSerialization(
            ktInstance = ValueClassList(listOf(ValueClassFromLib("42"), ValueClassFromLib("43"))),
            protocInstance = valueClassList {
                valueClassFromLibs += "42"
                valueClassFromLibs += "43"
            }
        )
    }
}
