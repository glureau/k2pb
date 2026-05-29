package sample.kt.collection

import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.ByteCollectionsProto
import com.glureau.k2pb_sample.NullableByteCollectionsProto
import com.glureau.sample.collection.ByteCollections
import com.glureau.sample.collection.NullableByteCollections
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class ByteCollectionTest : BaseEncodingTest() {

    @Test
    fun byteCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = ByteCollections(
                byteList = listOf(1, 127, -128),
                byteSet = setOf(Byte.MAX_VALUE, Byte.MIN_VALUE, 0),
            ),
            protocInstance = ByteCollectionsProto.ByteCollections.newBuilder()
                .addByteList(1)
                .addByteList(127)
                .addByteList(-128)
                .addByteSet(Byte.MAX_VALUE.toInt())
                .addByteSet(Byte.MIN_VALUE.toInt())
                .addByteSet(0)
                .build()
        )
    }

    @Test
    fun byteCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = ByteCollections(
                byteList = listOf(0, 0, 0),
                byteSet = setOf(0),
            ),
            protocInstance = ByteCollectionsProto.ByteCollections.newBuilder()
                .addByteList(0)
                .addByteList(0)
                .addByteList(0)
                .addByteSet(0)
                .build()
        )
    }

    @Test
    fun byteCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = ByteCollections(),
            protocInstance = ByteCollectionsProto.ByteCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun nullableByteCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = NullableByteCollections(
                nullableByteList = listOf(10, 20),
                nullableByteSet = setOf(30, 40),
            ),
            protocInstance = NullableByteCollectionsProto.NullableByteCollections.newBuilder()
                .addNullableByteList(10)
                .addNullableByteList(20)
                .setIsNullableByteListNull(NOT_NULL)
                .addNullableByteSet(30)
                .addNullableByteSet(40)
                .setIsNullableByteSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableByteCollections_null() {
        assertCompatibleSerialization(
            ktInstance = NullableByteCollections(
                nullableByteList = null,
                nullableByteSet = null,
            ),
            protocInstance = NullableByteCollectionsProto.NullableByteCollections.newBuilder()
                .setIsNullableByteListNull(NULL)
                .setIsNullableByteSetNull(NULL)
                .build()
        )
    }

    @Test
    fun nullableByteCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = NullableByteCollections(
                nullableByteList = listOf(0, 0),
                nullableByteSet = setOf(0),
            ),
            protocInstance = NullableByteCollectionsProto.NullableByteCollections.newBuilder()
                .addNullableByteList(0)
                .addNullableByteList(0)
                .setIsNullableByteListNull(NOT_NULL)
                .addNullableByteSet(0)
                .setIsNullableByteSetNull(NOT_NULL)
                .build()
        )
    }
}
