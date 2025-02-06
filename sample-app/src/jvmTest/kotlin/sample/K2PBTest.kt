package sample

import com.glureau.sample.AbstractClass
import com.glureau.sample.AbstractSubClass
import com.glureau.sample.Vehicle
import kotlin.test.Test
import kotlin.test.assertEquals

class K2PBTest {

    @Test
    fun `Vehicle polymorphic children are Bike and Car`() {
        assertEquals(
            expected = listOf(Vehicle.Car::class, Vehicle.Bike::class),
            actual = serializer.getRegisteredChildrenFor(Vehicle::class)
        )
    }

    @Test
    fun `AbstractClass polymorphic children are AbstractSubClass`() {
        assertEquals(
            expected = listOf(AbstractSubClass::class),
            actual = serializer.getRegisteredChildrenFor(AbstractClass::class)
        )
    }
}