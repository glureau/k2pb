package sample.kt

import com.glureau.k2pb.K2PBConstants.ExplicitNullability
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
                isUuidAsStringNull = ExplicitNullability.NOT_NULL
                uuidAsBytes = uuidToByteString("aa92e771-b29e-453a-b4f9-e984aa78d5aa")
                isUuidAsBytesNull = ExplicitNullability.NOT_NULL
                stringValueClass = "30dd3f74-5281-4f41-86b9-b7499c3a82ce"
                isStringValueClassNull = ExplicitNullability.NOT_NULL
                bytesValueClass = uuidToByteString("5513e592-65d1-4197-aeea-723bbc1cd14d")
                isBytesValueClassNull = ExplicitNullability.NOT_NULL
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
                isUuidAsStringNull = ExplicitNullability.NOT_NULL
                uuidAsBytes = UUIDfromLongs(0, 0)
                isUuidAsBytesNull = ExplicitNullability.NOT_NULL
                stringValueClass = "00000000-0000-0000-0000-000000000000"
                isStringValueClassNull = ExplicitNullability.NOT_NULL
                bytesValueClass = UUIDfromLongs(0, 0)
                isBytesValueClassNull = ExplicitNullability.NOT_NULL
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
                isUuidAsStringNull = ExplicitNullability.NULL
                uuidAsBytes = ByteString.EMPTY
                isUuidAsBytesNull = ExplicitNullability.NULL
                stringValueClass = ""
                isStringValueClassNull = ExplicitNullability.NULL
                bytesValueClass = ByteString.EMPTY
                isBytesValueClassNull = ExplicitNullability.NULL
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
        return bb.array().toByteString()
    }

}