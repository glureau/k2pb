package sample

import BarEventOuterClass
import CollectionTypeEventOuterClass
import CommentedClassOuterClass
import CommonClassOuterClass
import FooEventOuterClass
import UserOuterClass
import VehicleKt.car
import VehicleOuterClass
import WithNestClassAOuterClass
import com.glureau.sample.*
import org.junit.Test
import user

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
    fun checkSealedOneOf() {
        assertCompatibleSerialization(
            ktInstance = User(name = "Tony", vehicle = Vehicle.Car("Tesla")),
            protocInstance = UserOuterClass.User.newBuilder()
                .setName("Tony")
                .setCar(
                    VehicleOuterClass.Vehicle.Car.newBuilder()
                        .setBrand("Tesla")
                        .build()
                )
                .build()
        )

        assertCompatibleSerialization(
            ktInstance = User(name = "Francis", vehicle = Vehicle.Bike("Peugeot")),
            protocInstance = UserOuterClass.User.newBuilder()
                .setName("Francis")
                .setBike(
                    VehicleOuterClass.Vehicle.Bike.newBuilder()
                        .setBrand("Peugeot")
                        .build()
                )
                .build()
        )
    }

    @Test
    fun checkSealedOneOfWithProtocKotlinDsl() {
        assertCompatibleSerialization(
            ktInstance = User(name = "Tony", vehicle = Vehicle.Car("Tesla")),
            protocInstance = user {
                name = "Tony"
                car {
                    brand = "Tesla"
                }
            }
        )
    }
}
