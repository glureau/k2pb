package sample.kt.collection

import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.FloatCollectionsProto
import com.glureau.k2pb_sample.NullableFloatCollectionsProto
import com.glureau.sample.collection.FloatCollections
import com.glureau.sample.collection.NullableFloatCollections
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class FloatCollectionTest : BaseEncodingTest() {

    @Test
    fun floatCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = FloatCollections(
                floatList = listOf(1.5f, 2.7f, -3.14f),
                floatSet = setOf(Float.MAX_VALUE, Float.MIN_VALUE, 0.0f),
            ),
            protocInstance = FloatCollectionsProto.FloatCollections.newBuilder()
                .addFloatList(1.5f)
                .addFloatList(2.7f)
                .addFloatList(-3.14f)
                .addFloatSet(Float.MAX_VALUE)
                .addFloatSet(Float.MIN_VALUE)
                .addFloatSet(0.0f)
                .build()
        )
    }

    @Test
    fun floatCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = FloatCollections(
                floatList = listOf(0.0f, 0.0f, 0.0f),
                floatSet = setOf(0.0f),
            ),
            protocInstance = FloatCollectionsProto.FloatCollections.newBuilder()
                .addFloatList(0.0f)
                .addFloatList(0.0f)
                .addFloatList(0.0f)
                .addFloatSet(0.0f)
                .build()
        )
    }

    @Test
    fun floatCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = FloatCollections(),
            protocInstance = FloatCollectionsProto.FloatCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun nullableFloatCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = NullableFloatCollections(
                nullableFloatList = listOf(1.1f, 2.2f),
                nullableFloatSet = setOf(3.3f, 4.4f),
            ),
            protocInstance = NullableFloatCollectionsProto.NullableFloatCollections.newBuilder()
                .addNullableFloatList(1.1f)
                .addNullableFloatList(2.2f)
                .setIsNullableFloatListNull(NOT_NULL)
                .addNullableFloatSet(3.3f)
                .addNullableFloatSet(4.4f)
                .setIsNullableFloatSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableFloatCollections_null() {
        assertCompatibleSerialization(
            ktInstance = NullableFloatCollections(
                nullableFloatList = null,
                nullableFloatSet = null,
            ),
            protocInstance = NullableFloatCollectionsProto.NullableFloatCollections.newBuilder()
                .setIsNullableFloatListNull(NULL)
                .setIsNullableFloatSetNull(NULL)
                .build()
        )
    }

    @Test
    fun nullableFloatCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = NullableFloatCollections(
                nullableFloatList = listOf(0.0f, 0.0f),
                nullableFloatSet = setOf(0.0f),
            ),
            protocInstance = NullableFloatCollectionsProto.NullableFloatCollections.newBuilder()
                .addNullableFloatList(0.0f)
                .addNullableFloatList(0.0f)
                .setIsNullableFloatListNull(NOT_NULL)
                .addNullableFloatSet(0.0f)
                .setIsNullableFloatSetNull(NOT_NULL)
                .build()
        )
    }
}
