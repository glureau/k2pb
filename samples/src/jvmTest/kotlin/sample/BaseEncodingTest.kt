package sample

import com.google.protobuf.GeneratedMessageV3
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import org.junit.Assert.assertEquals
import kotlin.test.assertContentEquals

@OptIn(ExperimentalSerializationApi::class)
abstract class BaseEncodingTest {

    @PublishedApi
    internal val protoBuf: ProtoBuf = ProtoBuf {}

    inline fun <reified Kt : Any> assertCompatibleSerialization(
        ktInstance: Kt,
        protocInstance: GeneratedMessageV3,
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
