package sample.kt.collection

import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.NullableShortCollectionsProto
import com.glureau.k2pb_sample.ShortCollectionsProto
import com.glureau.sample.collection.NullableShortCollections
import com.glureau.sample.collection.ShortCollections
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class ShortCollectionTest : BaseEncodingTest() {

    @Test
    fun shortCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = ShortCollections(
                shortList = listOf(1, 100, -42),
                shortSet = setOf(Short.MAX_VALUE, Short.MIN_VALUE, 0),
            ),
            protocInstance = ShortCollectionsProto.ShortCollections.newBuilder()
                .addShortList(1)
                .addShortList(100)
                .addShortList(-42)
                .addShortSet(Short.MAX_VALUE.toInt())
                .addShortSet(Short.MIN_VALUE.toInt())
                .addShortSet(0)
                .build()
        )
    }

    @Test
    fun shortCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = ShortCollections(
                shortList = listOf(0, 0, 0),
                shortSet = setOf(0),
            ),
            protocInstance = ShortCollectionsProto.ShortCollections.newBuilder()
                .addShortList(0)
                .addShortList(0)
                .addShortList(0)
                .addShortSet(0)
                .build()
        )
    }

    @Test
    fun shortCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = ShortCollections(),
            protocInstance = ShortCollectionsProto.ShortCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun nullableShortCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = NullableShortCollections(
                nullableShortList = listOf(10, 20),
                nullableShortSet = setOf(30, 40),
            ),
            protocInstance = NullableShortCollectionsProto.NullableShortCollections.newBuilder()
                .addNullableShortList(10)
                .addNullableShortList(20)
                .setIsNullableShortListNull(NOT_NULL)
                .addNullableShortSet(30)
                .addNullableShortSet(40)
                .setIsNullableShortSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableShortCollections_null() {
        assertCompatibleSerialization(
            ktInstance = NullableShortCollections(
                nullableShortList = null,
                nullableShortSet = null,
            ),
            protocInstance = NullableShortCollectionsProto.NullableShortCollections.newBuilder()
                .setIsNullableShortListNull(NULL)
                .setIsNullableShortSetNull(NULL)
                .build()
        )
    }

    @Test
    fun nullableShortCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = NullableShortCollections(
                nullableShortList = listOf(0, 0),
                nullableShortSet = setOf(0),
            ),
            protocInstance = NullableShortCollectionsProto.NullableShortCollections.newBuilder()
                .addNullableShortList(0)
                .addNullableShortList(0)
                .setIsNullableShortListNull(NOT_NULL)
                .addNullableShortSet(0)
                .setIsNullableShortSetNull(NOT_NULL)
                .build()
        )
    }
}
