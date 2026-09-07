package sample.kt

import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.k2pb_sample.DeprecatedCollectionEndProto
import com.glureau.sample.CommonClass
import com.glureau.sample.DeprecatedCollectionEnd
import com.glureau.sample.DeprecatedCollectionStart
import org.junit.Test
import sample.kt.tools.BaseEncodingTest
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * A collection field removed from the Kotlin code must keep its 'repeated' label in the proto file,
 * otherwise the published schema no longer describes the data already on the wire.
 *
 * DeprecatedField.protoType is documented as the original proto type (string, int32, CommonClass...),
 * and there is no other place to express the cardinality.
 */
class DeprecatedCollectionMigrationTest : BaseEncodingTest() {

    private val protoFile = File(
        "build/generated/ksp/jvm/jvmMain/resources/k2pb/com/glureau/k2pb_sample/DeprecatedCollectionEnd.proto"
    ).readText()

    @Test
    fun `deprecated list of scalar is still repeated`() {
        assertTrue(
            protoFile.contains("repeated string names = 1;"),
            "'names' was a List<String>, it must stay repeated:\n$protoFile"
        )
    }

    @Test
    fun `deprecated list of message is still repeated`() {
        assertTrue(
            protoFile.contains("repeated CommonClass items = 2;"),
            "'items' was a List<CommonClass>, it must stay repeated:\n$protoFile"
        )
    }

    @Test
    fun `protoc sees the deprecated collections as repeated`() {
        val descriptor = DeprecatedCollectionEndProto.DeprecatedCollectionEnd.getDescriptor()
        assertTrue(descriptor.findFieldByName("names").isRepeated, "'names' should be repeated")
        assertTrue(descriptor.findFieldByName("items").isRepeated, "'items' should be repeated")
    }

    @Test
    fun `data encoded before the deprecation is still readable by protoc`() {
        val encoded = serializer.encodeToByteArray(
            DeprecatedCollectionStart(
                names = listOf("aaa", "bbb"),
                items = listOf(CommonClass("ccc")),
                b = "kept",
            )
        )

        val decoded = DeprecatedCollectionEndProto.DeprecatedCollectionEnd.parseFrom(encoded)
        val names = decoded.descriptorForType.findFieldByName("names")
        assertEquals(listOf("aaa", "bbb"), decoded.getField(names))
    }

    @Test
    fun `the remaining field is still decodable by K2PB`() {
        assertMigration(
            before = DeprecatedCollectionStart(
                names = listOf("aaa", "bbb"),
                items = listOf(CommonClass("ccc")),
                b = "kept",
            ),
            expectedAfter = DeprecatedCollectionEnd(b = "kept"),
        )
    }
}
