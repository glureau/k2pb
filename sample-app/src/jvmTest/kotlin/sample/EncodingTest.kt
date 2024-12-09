package sample

import AbstractSubClassOuterClass
import AnEnumOuterClass
import BarEventOuterClass
import BigDecimalHolderOuterClass
import BigDecimalValueClassHolderOuterClass
import CollectionTypeEventOuterClass
import CommentedClassOuterClass
import CommonClassOuterClass
import EnumHolderOuterClass
import FooEventOuterClass
import NullableBigDecimalHolderOuterClass
import NullableBigDecimalValueClassHolderOuterClass
import NullableValueClassHolderOuterClass
import ObjectClassOuterClass
import StandardClassOuterClass
import TransientFieldOuterClass
import ValueClassOfEnumHolderOuterClass
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
import com.glureau.sample.NullableBigDecimalHolder
import com.glureau.sample.NullableBigDecimalValueClass
import com.glureau.sample.NullableBigDecimalValueClassHolder
import com.glureau.sample.NullableValueClassHolder
import com.glureau.sample.ObjectClass
import com.glureau.sample.StandardClass
import com.glureau.sample.TransientField
import com.glureau.sample.User
import com.glureau.sample.Vehicle
import com.glureau.sample.WithNestClassA
import com.glureau.sample.lib.AnEnum
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.EnumHolder
import com.glureau.sample.lib.ValueClassFromLib
import com.glureau.sample.lib.ValueClassList
import com.glureau.sample.lib.ValueClassOfEnum
import com.glureau.sample.lib.ValueClassOfEnumHolder
import com.google.protobuf.kotlin.toByteString
import dataClassFromLib
import multiModule
import org.junit.Test
import sample.kt.tools.BaseEncodingTest
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
    fun checkCollectionTypeEvent() {
        assertCompatibleSerialization(
            ktInstance = CollectionTypeEvent(
                integerList = listOf(1, 3, 5),
                stringList = listOf("aaa", "bbb", "ccc"),
                maybeIntegerList = listOf(42, 51),
                mapStringInt = mapOf(
                    "a" to 2,
                    "b" to 4,
                ),
                dataClassList = listOf(DataClassFromLib(42)),
            ),
            protocInstance = CollectionTypeEventOuterClass.CollectionTypeEvent.newBuilder()
                .addIntegerList(1)
                .addDataClassList(
                    dataClassFromLib {
                        myInt = 42
                    }
                )
                .addIntegerList(3)
                .putMapStringInt("a", 2)
                .addIntegerList(5)
                .putMapStringInt("b", 4)
                .addMaybeIntegerList(42)
                .addMaybeIntegerList(51)
                .addStringList("aaa")
                .addStringList("bbb")
                .addStringList("ccc")
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
                .setIsBdNull(true)
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

    @Test
    fun nullableValueClass() {
        assertCompatibleSerialization(
            ktInstance = NullableValueClassHolder(ValueClassFromLib("42")),
            protocInstance = NullableValueClassHolderOuterClass.NullableValueClassHolder.newBuilder()
                .setValueClassFromLib("42")
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = NullableValueClassHolder(null),
            protocInstance = NullableValueClassHolderOuterClass.NullableValueClassHolder.newBuilder()
                .setValueClassFromLib("")
                .setIsValueClassFromLibNull(true)
                .build()
        )
        /*
        assertCompatibleSerialization(
            ktInstance = NullableValueClassHolder(ValueClassFromLib("")),
            protocInstance = NullableValueClassHolderOuterClass.NullableValueClassHolder.newBuilder()
                .setValueClassFromLib("")
                .setIsValueClassFromLibNull(false)
                .build()
        )*/ // Default value are still serialized on K2PB today
    }

    @Test
    fun enumHolder() {
        assertCompatibleSerialization(
            ktInstance = EnumHolder(AnEnum.AnEnum_A), // Default value, not encoded
            protocInstance = EnumHolderOuterClass.EnumHolder.newBuilder()
                .setValue(AnEnumOuterClass.AnEnum.AnEnum_A)
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = EnumHolder(AnEnum.AnEnum_B),
            protocInstance = EnumHolderOuterClass.EnumHolder.newBuilder()
                .setValue(AnEnumOuterClass.AnEnum.AnEnum_B)
                .build()
        )
    }

    @Test
    fun valueClassOfEnum() {
        assertCompatibleSerialization(
            ktInstance = ValueClassOfEnumHolder(ValueClassOfEnum(AnEnum.AnEnum_A)), // Default value, not encoded
            protocInstance = ValueClassOfEnumHolderOuterClass.ValueClassOfEnumHolder.newBuilder()
                .setValue(AnEnumOuterClass.AnEnum.AnEnum_A)
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = ValueClassOfEnumHolder(ValueClassOfEnum(AnEnum.AnEnum_B)),
            protocInstance = ValueClassOfEnumHolderOuterClass.ValueClassOfEnumHolder.newBuilder()
                .setValue(AnEnumOuterClass.AnEnum.AnEnum_B)
                .build()
        )
    }
}
