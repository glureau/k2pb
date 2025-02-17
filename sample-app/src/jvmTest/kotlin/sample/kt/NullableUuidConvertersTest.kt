package sample.kt

import com.glureau.k2pb_sample.nullableUuidsHolder
import com.glureau.sample.NullableUuidsHolder
import com.glureau.sample.UuidBytesValueClass
import com.glureau.sample.UuidStringValueClass
import com.google.protobuf.ByteString
import com.google.protobuf.kotlin.toByteString
import org.junit.Test
import sample.kt.tools.BaseEncodingTest
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.uuid.Uuid

class NullableUuidConvertersTest : BaseEncodingTest() {
    @Test
    fun data() {
        println(Uuid.random())
        assertCompatibleSerialization(
            ktInstance = NullableUuidsHolder(
                uuidAsString = Uuid.parse("1c92e771-b29e-453a-b4f9-e984aa78d5f0"),
                uuidAsBytes = Uuid.parse("aa92e771-b29e-453a-b4f9-e984aa78d5aa"),
                stringValueClass = UuidStringValueClass(
                    uuidAsString = Uuid.parse("30dd3f74-5281-4f41-86b9-b7499c3a82ce")
                ),
                bytesValueClass = UuidBytesValueClass(
                    uuidAsString = Uuid.parse("5513e592-65d1-4197-aeea-723bbc1cd14d")
                ),
            ),
            protocInstance = nullableUuidsHolder {
                uuidAsString = "1c92e771-b29e-453a-b4f9-e984aa78d5f0"
                uuidAsBytes = uuidToByteString("aa92e771-b29e-453a-b4f9-e984aa78d5aa")
                stringValueClass = "30dd3f74-5281-4f41-86b9-b7499c3a82ce"
                bytesValueClass = uuidToByteString("5513e592-65d1-4197-aeea-723bbc1cd14d")
            }
        )
    }

    @Test
    fun `zeroes are always encoded`() {
        assertCompatibleSerialization(
            ktInstance = NullableUuidsHolder(
                uuidAsString = Uuid.NIL,
                uuidAsBytes = Uuid.NIL,
                stringValueClass = UuidStringValueClass(
                    uuidAsString = Uuid.NIL,
                ),
                bytesValueClass = UuidBytesValueClass(
                    uuidAsString = Uuid.NIL,
                ),
            ),
            protocInstance = nullableUuidsHolder {
                uuidAsString = "00000000-0000-0000-0000-000000000000"
                uuidAsBytes = UUIDfromLongs(0, 0)
                stringValueClass = "00000000-0000-0000-0000-000000000000"
                bytesValueClass = UUIDfromLongs(0, 0)
            }
        )
    }

    @Test
    fun `null are not encoded`() {
        assertCompatibleSerialization(
            ktInstance = NullableUuidsHolder(
                uuidAsString = null,
                uuidAsBytes = null,
                stringValueClass = null,
                bytesValueClass = null,
            ),
            protocInstance = nullableUuidsHolder {
                uuidAsString = ""
                isUuidAsStringNull = true
                uuidAsBytes = ByteString.EMPTY
                isUuidAsBytesNull = true
                stringValueClass = ""
                isStringValueClassNull = true
                bytesValueClass = ByteString.EMPTY
                isBytesValueClassNull = true
            }
        )
    }

    private fun uuidToByteString(uuidStr: String): ByteString {
        val uuid = UUID.fromString(uuidStr)
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array().toByteString()
    }

    private fun UUIDfromLongs(mostSignificantBits: Long, leastSignificantBits: Long): ByteString {
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(mostSignificantBits)
        bb.putLong(leastSignificantBits)
        val uuid = UUID.nameUUIDFromBytes(bb.array())
        return bb.array().toByteString()
    }

}