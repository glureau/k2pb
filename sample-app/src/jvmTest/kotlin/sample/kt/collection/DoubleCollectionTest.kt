package sample.kt.collection

import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.DoubleCollectionsProto
import com.glureau.k2pb_sample.NullableDoubleCollectionsProto
import com.glureau.sample.collection.DoubleCollections
import com.glureau.sample.collection.NullableDoubleCollections
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class DoubleCollectionTest : BaseEncodingTest() {

    @Test
    fun doubleCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = DoubleCollections(
                doubleList = listOf(1.5, 2.7, -3.14),
                doubleSet = setOf(Double.MAX_VALUE, Double.MIN_VALUE, 0.0),
            ),
            protocInstance = DoubleCollectionsProto.DoubleCollections.newBuilder()
                .addDoubleList(1.5)
                .addDoubleList(2.7)
                .addDoubleList(-3.14)
                .addDoubleSet(Double.MAX_VALUE)
                .addDoubleSet(Double.MIN_VALUE)
                .addDoubleSet(0.0)
                .build()
        )
    }

    @Test
    fun doubleCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = DoubleCollections(
                doubleList = listOf(0.0, 0.0, 0.0),
                doubleSet = setOf(0.0),
            ),
            protocInstance = DoubleCollectionsProto.DoubleCollections.newBuilder()
                .addDoubleList(0.0)
                .addDoubleList(0.0)
                .addDoubleList(0.0)
                .addDoubleSet(0.0)
                .build()
        )
    }

    @Test
    fun doubleCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = DoubleCollections(),
            protocInstance = DoubleCollectionsProto.DoubleCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun nullableDoubleCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = NullableDoubleCollections(
                nullableDoubleList = listOf(1.1, 2.2),
                nullableDoubleSet = setOf(3.3, 4.4),
            ),
            protocInstance = NullableDoubleCollectionsProto.NullableDoubleCollections.newBuilder()
                .addNullableDoubleList(1.1)
                .addNullableDoubleList(2.2)
                .setIsNullableDoubleListNull(NOT_NULL)
                .addNullableDoubleSet(3.3)
                .addNullableDoubleSet(4.4)
                .setIsNullableDoubleSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableDoubleCollections_null() {
        assertCompatibleSerialization(
            ktInstance = NullableDoubleCollections(
                nullableDoubleList = null,
                nullableDoubleSet = null,
            ),
            protocInstance = NullableDoubleCollectionsProto.NullableDoubleCollections.newBuilder()
                .setIsNullableDoubleListNull(NULL)
                .setIsNullableDoubleSetNull(NULL)
                .build()
        )
    }

    @Test
    fun nullableDoubleCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = NullableDoubleCollections(
                nullableDoubleList = listOf(0.0, 0.0),
                nullableDoubleSet = setOf(0.0),
            ),
            protocInstance = NullableDoubleCollectionsProto.NullableDoubleCollections.newBuilder()
                .addNullableDoubleList(0.0)
                .addNullableDoubleList(0.0)
                .setIsNullableDoubleListNull(NOT_NULL)
                .addNullableDoubleSet(0.0)
                .setIsNullableDoubleSetNull(NOT_NULL)
                .build()
        )
    }
}
