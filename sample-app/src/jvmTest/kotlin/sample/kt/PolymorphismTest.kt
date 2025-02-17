package sample.kt

import com.glureau.k2pb_sample.AbstractSubClassProto
import com.glureau.k2pb_sample.VehicleKt.bike
import com.glureau.k2pb_sample.VehicleKt.car
import com.glureau.k2pb_sample.abstractClass
import com.glureau.k2pb_sample.abstractSubClass
import com.glureau.k2pb_sample.user
import com.glureau.k2pb_sample.vehicle
import com.glureau.sample.AbstractClass
import com.glureau.sample.AbstractSubClass
import com.glureau.sample.User
import com.glureau.sample.Vehicle
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class PolymorphismTest : BaseEncodingTest() {

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
    fun defaultValues() {
        assertCompatibleSerialization(
            ktInstance = User(name = "", vehicle = Vehicle.Bike("")),
            protocInstance = user {
                name = ""
                vehicle = vehicle {
                    bike = bike {
                        brand = ""
                    }
                }
            }
        )
    }

    @Test
    fun checkAbstractSubClass() {
        assertCompatibleSerialization(
            ktInstance = AbstractSubClass(foo = 2, bar = "asc"),
            protocInstance = AbstractSubClassProto.AbstractSubClass.newBuilder().setFoo(2).setBar("asc").build(),
        )
    }

    @Test
    fun checkAbstractClass() {
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
    fun checkAbstractClassWithDefaultValues() {
        // de/serialization is done via AbstractClass polymorphism
        assertCompatibleSerialization<AbstractClass>(
            ktInstance = AbstractSubClass(foo = 0, bar = "") as AbstractClass,
            protocInstance = abstractClass {
                abstractSubClass = abstractSubClass {
                    foo = 0
                    bar = ""
                }
            }
        )
    }
}