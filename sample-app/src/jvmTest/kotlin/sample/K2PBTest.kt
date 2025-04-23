package sample

import com.glureau.k2pb.runtime.K2PB
import com.glureau.sample.AbstractClass
import com.glureau.sample.AbstractSubClass
import com.glureau.sample.Vehicle
import com.glureau.sample.lib.registerSampleLibCodecs
import com.glureau.sample.registerSampleAppCodecs
import kotlin.test.Test
import kotlin.test.assertEquals

class K2PBTest {
    val serializer = K2PB {
        registerSampleLibCodecs()
        registerSampleAppCodecs()
    }

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