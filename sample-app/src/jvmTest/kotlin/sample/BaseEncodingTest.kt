package sample

import com.glureau.k2pb.runtime.K2PB
import com.glureau.sample.AbstractClass
import com.glureau.sample.AbstractSubClass
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
    val serializer = K2PB()

    inline fun <reified Kt : Any> assertCompatibleSerialization(
        ktInstance: Kt,
        protocInstance: GeneratedMessage,
    ) {

        val encodedViaKtxSerialization = serializer.encodeToByteArray<Kt>(ktInstance)
        val encodedViaProtocGeneratedCode = protocInstance.toByteArray()

        assertContentEquals(expected = encodedViaKtxSerialization, actual = encodedViaProtocGeneratedCode)

        val decodedViaKtxSerialization = serializer.decodeFromByteArray<Kt>(encodedViaProtocGeneratedCode)
        val decodedViaProtocGeneratedCode = protocInstance.parserForType.parseFrom(encodedViaKtxSerialization)

        // Asserting that data encoded from protoc generated files and decoded via ktx serialization are equals.
        assertEquals(ktInstance, decodedViaKtxSerialization)
        // Asserting that data encoded from ktx serialization and decoded via protoc generated files are equals.
        assertEquals(protocInstance, decodedViaProtocGeneratedCode)
    }
}
