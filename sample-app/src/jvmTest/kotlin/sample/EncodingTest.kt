package sample

import BarEventOuterClass
import CollectionTypeEventOuterClass
import CommentedClassOuterClass
import CommonClassOuterClass
import FooEventOuterClass
import VehicleKt.bike
import VehicleKt.car
import WithNestClassAOuterClass
import com.glureau.sample.*
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import dataClassFromLib
import multiModule
import org.junit.Test
import user
import vehicle

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
                mapStringInt = mapOf(
                    "a" to 2,
                    "b" to 4,
                ),
            ),
            protocInstance = CollectionTypeEventOuterClass.CollectionTypeEvent.newBuilder()
                .addIntegerList(1)
                .addIntegerList(3)
                .addIntegerList(5)
                .putMapStringInt("a", 2)
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
                    type = "Vehicle.Car"
                    value = car {
                        brand = "Tesla"
                    }.toByteString()
                }
            }
        )

        assertCompatibleSerialization(
            ktInstance = User(name = "Francis", vehicle = Vehicle.Bike("Peugeot")),
            protocInstance = user {
                name = "Francis"
                vehicle = vehicle {
                    type = "Vehicle.Bike"
                    value = bike {
                        brand = "Peugeot"
                    }.toByteString()
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
            ktInstance = BigDecimalHolder(java.math.BigDecimal("42.42")),
            protocInstance = BigDecimalHolderOuterClass.BigDecimalHolder.newBuilder()
                .setBd("42.42")
                .build()
        )
    }
}
