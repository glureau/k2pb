package sample.kt

import com.glureau.k2pb_sample.uuidsHolder
import com.glureau.sample.UuidBytesValueClass
import com.glureau.sample.UuidStringValueClass
import com.glureau.sample.UuidsHolder
import com.google.protobuf.ByteString
import com.google.protobuf.kotlin.toByteString
import org.junit.Test
import sample.kt.tools.BaseEncodingTest
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.uuid.Uuid

class UuidConvertersTest : BaseEncodingTest() {
    @Test
    fun data() {
        println(Uuid.random())
        assertCompatibleSerialization(
            ktInstance = UuidsHolder(
                uuidAsString = Uuid.parse("1c92e771-b29e-453a-b4f9-e984aa78d5f0"),
                uuidAsBytes = Uuid.parse("aa92e771-b29e-453a-b4f9-e984aa78d5aa"),
                stringValueClass = UuidStringValueClass(
                    uuidAsString = Uuid.parse("30dd3f74-5281-4f41-86b9-b7499c3a82ce")
                ),
                bytesValueClass = UuidBytesValueClass(
                    uuidAsString = Uuid.parse("5513e592-65d1-4197-aeea-723bbc1cd14d")
                ),
            ),
            protocInstance = uuidsHolder {
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
            ktInstance = UuidsHolder(
                uuidAsString = Uuid.NIL,
                uuidAsBytes = Uuid.NIL,
                stringValueClass = UuidStringValueClass(
                    uuidAsString = Uuid.NIL,
                ),
                bytesValueClass = UuidBytesValueClass(
                    uuidAsString = Uuid.NIL,
                ),
            ),
            protocInstance = uuidsHolder {
                uuidAsString = "00000000-0000-0000-0000-000000000000"
                uuidAsBytes = UUIDfromLongs(0, 0)
                stringValueClass = "00000000-0000-0000-0000-000000000000"
                bytesValueClass = UUIDfromLongs(0, 0)
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