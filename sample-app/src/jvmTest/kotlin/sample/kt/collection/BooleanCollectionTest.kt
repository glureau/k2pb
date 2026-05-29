package sample.kt.collection

import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.BooleanCollectionsProto
import com.glureau.k2pb_sample.NullableBooleanCollectionsProto
import com.glureau.sample.collection.BooleanCollections
import com.glureau.sample.collection.NullableBooleanCollections
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class BooleanCollectionTest : BaseEncodingTest() {

    @Test
    fun booleanCollections_allTrue() {
        assertCompatibleSerialization(
            ktInstance = BooleanCollections(
                booleanList = listOf(true, true, true),
                booleanSet = setOf(true),
            ),
            protocInstance = BooleanCollectionsProto.BooleanCollections.newBuilder()
                .addBooleanList(true)
                .addBooleanList(true)
                .addBooleanList(true)
                .addBooleanSet(true)
                .build()
        )
    }

    @Test
    fun booleanCollections_allFalse() {
        assertCompatibleSerialization(
            ktInstance = BooleanCollections(
                booleanList = listOf(false, false, false),
                booleanSet = setOf(false),
            ),
            protocInstance = BooleanCollectionsProto.BooleanCollections.newBuilder()
                .addBooleanList(false)
                .addBooleanList(false)
                .addBooleanList(false)
                .addBooleanSet(false)
                .build()
        )
    }

    @Test
    fun booleanCollections_mixed() {
        assertCompatibleSerialization(
            ktInstance = BooleanCollections(
                booleanList = listOf(true, false, true, false),
                booleanSet = setOf(true, false),
            ),
            protocInstance = BooleanCollectionsProto.BooleanCollections.newBuilder()
                .addBooleanList(true)
                .addBooleanList(false)
                .addBooleanList(true)
                .addBooleanList(false)
                .addBooleanSet(true)
                .addBooleanSet(false)
                .build()
        )
    }

    @Test
    fun booleanCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = BooleanCollections(),
            protocInstance = BooleanCollectionsProto.BooleanCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun booleanCollections_singleFalse() {
        assertCompatibleSerialization(
            ktInstance = BooleanCollections(booleanList = listOf(false)),
            protocInstance = BooleanCollectionsProto.BooleanCollections.newBuilder()
                .addBooleanList(false)
                .build()
        )
    }

    @Test
    fun booleanCollections_singleTrue() {
        assertCompatibleSerialization(
            ktInstance = BooleanCollections(booleanList = listOf(true)),
            protocInstance = BooleanCollectionsProto.BooleanCollections.newBuilder()
                .addBooleanList(true)
                .build()
        )
    }

    @Test
    fun nullableBooleanCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = NullableBooleanCollections(
                nullableBooleanList = listOf(true, false, true),
                nullableBooleanSet = setOf(true, false),
            ),
            protocInstance = NullableBooleanCollectionsProto.NullableBooleanCollections.newBuilder()
                .addNullableBooleanList(true)
                .addNullableBooleanList(false)
                .addNullableBooleanList(true)
                .setIsNullableBooleanListNull(NOT_NULL)
                .addNullableBooleanSet(true)
                .addNullableBooleanSet(false)
                .setIsNullableBooleanSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableBooleanCollections_null() {
        assertCompatibleSerialization(
            ktInstance = NullableBooleanCollections(
                nullableBooleanList = null,
                nullableBooleanSet = null,
            ),
            protocInstance = NullableBooleanCollectionsProto.NullableBooleanCollections.newBuilder()
                .setIsNullableBooleanListNull(NULL)
                .setIsNullableBooleanSetNull(NULL)
                .build()
        )
    }
}
