package sample

import com.glureau.k2pb.ProtoPolymorphism
import com.glureau.k2pb.ProtoPolymorphism.Pair
import com.glureau.k2pb.runtime.K2PB
import com.glureau.k2pb.runtime.decodeFromByteArray
import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.sample.AbstractClass
import com.glureau.sample.AbstractSubClass
import com.glureau.sample.lib.registerSampleLibSerializers
import com.glureau.sample.registerSampleAppSerializers
import com.google.protobuf.GeneratedMessage
import org.junit.Assert.assertEquals
import kotlin.test.assertContentEquals

@ProtoPolymorphism(
    AbstractClass::class,
    [Pair(AbstractSubClass::class, 1)]
)
private object K2PBPolymorphismConfigHolder

abstract class BaseEncodingTest {

    val serializer = K2PB {
        registerSampleLibSerializers()
        registerSampleAppSerializers()
        // TODO: Rewrite that in the generated serializer aggregator or even remove it if not required anymore
        //  if the generated polymorphic serializer.
        /*
        registerPolymorphicDefinition(
            AbstractClass::class, mapOf(
                AbstractSubClass::class to 1
            )
        )
        // TODO: Sealed class could be automatically generated
        registerPolymorphicDefinition(
            Vehicle::class, mapOf(
                Vehicle.Car::class to 1,
                Vehicle.Bike::class to 2,
            )
        )
        */
    }

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified Kt : Any> assertCompatibleSerialization(
        ktInstance: Kt,
        protocInstance: GeneratedMessage,
    ) {
        val encodedViaProtocGeneratedCode = protocInstance.toByteArray()
        println("encodedViaProtocGeneratedCode\t ${encodedViaProtocGeneratedCode.joinToString(" ") { it.toHexString() }}")
        val encodedViaK2PB = serializer.encodeToByteArray<Kt>(ktInstance)
        println("encodedViaK2PB\t\t\t\t\t ${encodedViaK2PB.joinToString(" ") { it.toHexString() }}")
        assertContentEquals(expected = encodedViaK2PB, actual = encodedViaProtocGeneratedCode)

        val decodedViaKtxSerialization = serializer.decodeFromByteArray<Kt>(encodedViaProtocGeneratedCode)
        val decodedViaProtocGeneratedCode = protocInstance.parserForType.parseFrom(encodedViaK2PB)

        // Asserting that data encoded from protoc generated files and decoded via ktx serialization are equals.
        assertEquals(ktInstance, decodedViaKtxSerialization)
        // Asserting that data encoded from ktx serialization and decoded via protoc generated files are equals.
        assertEquals(protocInstance, decodedViaProtocGeneratedCode)

        println("Original Kt: $ktInstance")
        println("Decoded Kt: $decodedViaKtxSerialization")
    }
}
