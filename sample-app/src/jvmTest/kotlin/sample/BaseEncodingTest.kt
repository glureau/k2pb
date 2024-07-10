package sample

import com.glureau.sample.AbstractClass
import com.glureau.sample.AbstractSubClass
import com.google.protobuf.GeneratedMessage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.Assert.assertEquals
import kotlin.test.assertContentEquals

@OptIn(ExperimentalSerializationApi::class)
abstract class BaseEncodingTest {

    @PublishedApi
    internal val protoBuf: ProtoBuf = ProtoBuf {
        encodeDefaults = true
        serializersModule = SerializersModule {
            polymorphic(AbstractClass::class) {
                subclass(AbstractSubClass::class)
            }
        }
    }

    inline fun <reified Kt : Any> assertCompatibleSerialization(
        ktInstance: Kt,
        protocInstance: GeneratedMessage,
    ) {
        val encodedViaKtxSerialization = protoBuf.encodeToByteArray<Kt>(ktInstance)
        val encodedViaProtocGeneratedCode = protocInstance.toByteArray()

        assertContentEquals(expected = encodedViaKtxSerialization, actual = encodedViaProtocGeneratedCode)

        val decodedViaKtxSerialization = protoBuf.decodeFromByteArray<Kt>(encodedViaProtocGeneratedCode)
        val decodedViaProtocGeneratedCode = protocInstance.parserForType.parseFrom(encodedViaKtxSerialization)

        // Asserting that data encoded from protoc generated files and decoded via ktx serialization are equals.
        assertEquals(ktInstance, decodedViaKtxSerialization)
        // Asserting that data encoded from ktx serialization and decoded via protoc generated files are equals.
        assertEquals(protocInstance, decodedViaProtocGeneratedCode)
    }
}
