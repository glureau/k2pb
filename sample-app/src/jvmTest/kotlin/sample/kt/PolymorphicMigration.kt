package sample.kt

import com.glureau.k2pb.runtime.decodeFromByteArray
import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.k2pb_sample.PolymorphicMigrationKt.five
import com.glureau.k2pb_sample.PolymorphicMigrationKt.one
import com.glureau.k2pb_sample.PolymorphicMigrationKt.six
import com.glureau.k2pb_sample.PolymorphicMigrationKt.two
import com.glureau.k2pb_sample.PolymorphicMigrationProto
import com.glureau.k2pb_sample.polymorphicMigration
import com.glureau.sample.PolymorphicMigration
import org.junit.Test
import sample.kt.tools.BaseEncodingTest
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PolymorphicMigrationTest : BaseEncodingTest() {

    @Test
    fun one() {
        assertCompatibleSerialization<PolymorphicMigration>(
            ktInstance = PolymorphicMigration.One,
            protocInstance = polymorphicMigration {
                one = one {}
            }
        )
    }

    @Test
    fun two() {
        assertCompatibleSerialization<PolymorphicMigration>(
            ktInstance = PolymorphicMigration.Two("two"),
            protocInstance = polymorphicMigration {
                two = two { a = "two" }
            }
        )
    }

    // 3 : Not much to test here, the field is fully removed...
    // 4 : This field never existed anyway.

    @Test
    fun five() {
        assertCompatibleSerialization<PolymorphicMigration>(
            ktInstance = PolymorphicMigration.Five(5L),
            protocInstance = polymorphicMigration {
                five = five { b = 5L }
            }
        )
    }

    @Test
    fun six() {
        // 32 02 08 06 SHOULD BE decodable by protoc as a six with value 6
        val byteArray = byteArrayOf(0x32, 0x02, 0x08, 0x06)
        val decodedViaK2PB = serializer.decodeFromByteArray<PolymorphicMigration>(byteArray)
        println("$decodedViaK2PB should be null")
        assertNull(decodedViaK2PB) // Six is not supported anymore
        val decodedViaProtocGeneratedCode = PolymorphicMigrationProto.PolymorphicMigration.parseFrom(byteArray)
        println("Protoc retrocompat supported: $decodedViaProtocGeneratedCode")
        assertEquals(polymorphicMigration {
            six = six { b = 6L }
        }, decodedViaProtocGeneratedCode)

        // K2PB should generate an empty array there, as it's not a declared child anymore
        val encodedViaK2PB = serializer.encodeToByteArray<PolymorphicMigration>(PolymorphicMigration.DeprecatedSix(6L))
        assert(encodedViaK2PB.isEmpty())
    }
}
