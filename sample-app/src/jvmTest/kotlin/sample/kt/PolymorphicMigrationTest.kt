package sample.kt

import com.glureau.k2pb.runtime.decodeFromByteArray
import com.glureau.k2pb_sample.PolymorphicMigrationKt.five
import com.glureau.k2pb_sample.PolymorphicMigrationKt.one
import com.glureau.k2pb_sample.PolymorphicMigrationKt.seven
import com.glureau.k2pb_sample.PolymorphicMigrationKt.six
import com.glureau.k2pb_sample.PolymorphicMigrationKt.two
import com.glureau.k2pb_sample.PolymorphicMigrationProto
import com.glureau.k2pb_sample.polymorphicMigration
import com.glureau.sample.PolymorphicMigration
import org.junit.Test
import sample.kt.tools.BaseEncodingTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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


    @Test
    fun three() {
        // 1A 02 08 03 SHOULD BE decodable by protoc as a three with value 3
        val byteArray = byteArrayOf(0x1A, 0x02, 0x08, 0x03)
        val decodedViaK2PB = serializer.decodeFromByteArray<PolymorphicMigration>(byteArray)
        println("$decodedViaK2PB should be null")
        assertNull(decodedViaK2PB) // Three is not supported anymore
        val decodedViaProtocGeneratedCode = PolymorphicMigrationProto.PolymorphicMigration.parseFrom(byteArray)
        println("Protoc: $decodedViaProtocGeneratedCode")
        // Protoc retrocompat is not supported anymore, but we can still get the data from the unknown fields
        assertNotNull(decodedViaProtocGeneratedCode)
        assertEquals(
            expected = """|3: {
                          |  1: 3
                          |}
                          |""".trimMargin(),
            actual = decodedViaProtocGeneratedCode.unknownFields.toString()
        )
    }
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
    fun `Six is migrated to Seven`() {
        // decodable by protoc as a six with value 6, and migrated to 7 by the K2PB custom decoder
        val legacyThree = polymorphicMigration {
            six = six {
                b = 6
            }
        }

        val byteArray = legacyThree.toByteArray()

        val decodedViaK2PB = serializer.decodeFromByteArray<PolymorphicMigration>(byteArray)
        println("$decodedViaK2PB should be null")
        assertEquals(PolymorphicMigration.Seven("6"), decodedViaK2PB)

        val decodedViaProtocGeneratedCode = PolymorphicMigrationProto.PolymorphicMigration.parseFrom(byteArray)
        println("Protoc retrocompat supported: $decodedViaProtocGeneratedCode")
        assertEquals(polymorphicMigration {
            six = six { b = 6L }
        }, decodedViaProtocGeneratedCode)

        // Six being removed from code, K2PB can't produce it anymore
    }

    @Test
    fun seven() {
        // No matter if Six has been migrated to Seven, Seven is still working independently
        assertCompatibleSerialization<PolymorphicMigration>(
            ktInstance = PolymorphicMigration.Seven("hello"),
            protocInstance = polymorphicMigration {
                seven = seven {
                    b = "hello"
                }
            }
        )
    }
}
