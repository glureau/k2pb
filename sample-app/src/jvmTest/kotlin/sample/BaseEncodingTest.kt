package sample

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


abstract class BaseEncodingTest {

    /*
    @PublishedApi
    internal val protoBuf: ProtoBuf = ProtoBuf {
        encodeDefaults = true
        serializersModule = SerializersModule {
            polymorphic(AbstractClass::class) {
                subclass(AbstractSubClass::class)
            }
        }
    }
    */
    val serializer = K2PB {
        registerSampleLibSerializers()
        registerSampleAppSerializers()
        registerPolymorphicDefinition(
            AbstractClass::class, mapOf(
                AbstractSubClass::class to 1
            )
        )
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
