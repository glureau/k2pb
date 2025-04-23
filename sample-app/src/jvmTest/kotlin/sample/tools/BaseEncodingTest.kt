package sample.kt.tools

import com.glureau.k2pb.runtime.K2PB
import com.glureau.k2pb.runtime.decodeFromByteArray
import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.sample.lib.registerSampleLibCodecs
import com.glureau.sample.registerSampleAppCodecs
import com.google.protobuf.GeneratedMessage
import org.junit.Assert
import kotlin.test.assertContentEquals

abstract class BaseEncodingTest {

    val serializer = K2PB {
        registerSampleLibCodecs()
        registerSampleAppCodecs()
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

        val decodedViaK2PB = serializer.decodeFromByteArray<Kt>(encodedViaProtocGeneratedCode)
        val decodedViaProtocGeneratedCode = protocInstance.parserForType.parseFrom(encodedViaK2PB)
        println("Original Kt: $ktInstance")
        println("Decoded Kt : $decodedViaK2PB")

        // Asserting that data encoded from protoc generated files and decoded via ktx serialization are equals.
        Assert.assertEquals(ktInstance, decodedViaK2PB)
        // Asserting that data encoded from ktx serialization and decoded via protoc generated files are equals.
        Assert.assertEquals(protocInstance, decodedViaProtocGeneratedCode)
    }


    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified Before : Any, reified After : Any> assertMigration(
        before: Before,
        expectedAfter: After,
    ) {
        val encoded = serializer.encodeToByteArray<Before>(before)
        println("encoded\t ${encoded.joinToString(" ") { it.toHexString() }}")
        val decoded = serializer.decodeFromByteArray<After>(encoded)
        println("Expected: $expectedAfter")
        println("Decoded:  $decoded")
        Assert.assertEquals(expectedAfter, decoded)
    }
}