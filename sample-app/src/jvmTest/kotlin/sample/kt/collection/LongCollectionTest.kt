package sample.kt.collection

import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.LongCollectionsProto
import com.glureau.k2pb_sample.NullableLongCollectionsProto
import com.glureau.sample.collection.LongCollections
import com.glureau.sample.collection.NullableLongCollections
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class LongCollectionTest : BaseEncodingTest() {

    @Test
    fun longCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = LongCollections(
                longList = listOf(1L, 100_000_000_000L, -42L),
                longSet = setOf(Long.MAX_VALUE, Long.MIN_VALUE, 0L),
            ),
            protocInstance = LongCollectionsProto.LongCollections.newBuilder()
                .addLongList(1L)
                .addLongList(100_000_000_000L)
                .addLongList(-42L)
                .addLongSet(Long.MAX_VALUE)
                .addLongSet(Long.MIN_VALUE)
                .addLongSet(0L)
                .build()
        )
    }

    @Test
    fun longCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = LongCollections(
                longList = listOf(0L, 0L, 0L),
                longSet = setOf(0L),
            ),
            protocInstance = LongCollectionsProto.LongCollections.newBuilder()
                .addLongList(0L)
                .addLongList(0L)
                .addLongList(0L)
                .addLongSet(0L)
                .build()
        )
    }

    @Test
    fun longCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = LongCollections(),
            protocInstance = LongCollectionsProto.LongCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun nullableLongCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = NullableLongCollections(
                nullableLongList = listOf(99L, -1L),
                nullableLongSet = setOf(7L, 8L),
            ),
            protocInstance = NullableLongCollectionsProto.NullableLongCollections.newBuilder()
                .addNullableLongList(99L)
                .addNullableLongList(-1L)
                .setIsNullableLongListNull(NOT_NULL)
                .addNullableLongSet(7L)
                .addNullableLongSet(8L)
                .setIsNullableLongSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableLongCollections_null() {
        assertCompatibleSerialization(
            ktInstance = NullableLongCollections(
                nullableLongList = null,
                nullableLongSet = null,
            ),
            protocInstance = NullableLongCollectionsProto.NullableLongCollections.newBuilder()
                .setIsNullableLongListNull(NULL)
                .setIsNullableLongSetNull(NULL)
                .build()
        )
    }

    @Test
    fun nullableLongCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = NullableLongCollections(
                nullableLongList = listOf(0L, 0L),
                nullableLongSet = setOf(0L),
            ),
            protocInstance = NullableLongCollectionsProto.NullableLongCollections.newBuilder()
                .addNullableLongList(0L)
                .addNullableLongList(0L)
                .setIsNullableLongListNull(NOT_NULL)
                .addNullableLongSet(0L)
                .setIsNullableLongSetNull(NOT_NULL)
                .build()
        )
    }
}
