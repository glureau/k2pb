package sample.kt.collection

import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.IntCollectionsProto
import com.glureau.k2pb_sample.NullableIntCollectionsProto
import com.glureau.sample.collection.IntCollections
import com.glureau.sample.collection.NullableIntCollections
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class IntCollectionTest : BaseEncodingTest() {

    @Test
    fun intCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = IntCollections(
                integerList = listOf(1, 3, 5),
                integerSet = setOf(100, 1, 3),
            ),
            protocInstance = IntCollectionsProto.IntCollections.newBuilder()
                .addIntegerList(1)
                .addIntegerList(3)
                .addIntegerList(5)
                .addIntegerSet(100)
                .addIntegerSet(1)
                .addIntegerSet(3)
                .build()
        )
    }

    @Test
    fun intCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = IntCollections(
                integerList = listOf(0, 0, 0),
                integerSet = setOf(0),
            ),
            protocInstance = IntCollectionsProto.IntCollections.newBuilder()
                .addIntegerList(0)
                .addIntegerList(0)
                .addIntegerList(0)
                .addIntegerSet(0)
                .build()
        )
    }

    @Test
    fun intCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = IntCollections(),
            protocInstance = IntCollectionsProto.IntCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun nullableIntCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = NullableIntCollections(
                nullableIntegerList = listOf(42, 51),
                nullableIntegerSet = setOf(39, 38),
            ),
            protocInstance = NullableIntCollectionsProto.NullableIntCollections.newBuilder()
                .addNullableIntegerList(42)
                .addNullableIntegerList(51)
                .setIsNullableIntegerListNull(NOT_NULL)
                .addNullableIntegerSet(39)
                .addNullableIntegerSet(38)
                .setIsNullableIntegerSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableIntCollections_null() {
        assertCompatibleSerialization(
            ktInstance = NullableIntCollections(
                nullableIntegerList = null,
                nullableIntegerSet = null,
            ),
            protocInstance = NullableIntCollectionsProto.NullableIntCollections.newBuilder()
                .setIsNullableIntegerListNull(NULL)
                .setIsNullableIntegerSetNull(NULL)
                .build()
        )
    }

    @Test
    fun nullableIntCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = NullableIntCollections(
                nullableIntegerList = listOf(0, 0),
                nullableIntegerSet = setOf(0),
            ),
            protocInstance = NullableIntCollectionsProto.NullableIntCollections.newBuilder()
                .addNullableIntegerList(0)
                .addNullableIntegerList(0)
                .setIsNullableIntegerListNull(NOT_NULL)
                .addNullableIntegerSet(0)
                .setIsNullableIntegerSetNull(NOT_NULL)
                .build()
        )
    }
}
