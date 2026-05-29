package sample.kt.collection

import com.glureau.custom.javapackage.dataClassFromLib
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.DataClassCollectionsProto
import com.glureau.k2pb_sample.NullableDataClassCollectionsProto
import com.glureau.sample.collection.DataClassCollections
import com.glureau.sample.collection.NullableDataClassCollections
import com.glureau.sample.lib.DataClassFromLib
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class DataClassCollectionTest : BaseEncodingTest() {

    @Test
    fun dataClassCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = DataClassCollections(
                dataClassList = listOf(DataClassFromLib(33), DataClassFromLib(34)),
                dataClassSet = setOf(DataClassFromLib(37), DataClassFromLib(36)),
            ),
            protocInstance = DataClassCollectionsProto.DataClassCollections.newBuilder()
                .addDataClassList(dataClassFromLib { myInt = 33 })
                .addDataClassList(dataClassFromLib { myInt = 34 })
                .addDataClassSet(dataClassFromLib { myInt = 37 })
                .addDataClassSet(dataClassFromLib { myInt = 36 })
                .build()
        )
    }

    @Test
    fun dataClassCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = DataClassCollections(
                dataClassList = listOf(DataClassFromLib(0)),
                dataClassSet = setOf(DataClassFromLib(0)),
            ),
            protocInstance = DataClassCollectionsProto.DataClassCollections.newBuilder()
                .addDataClassList(dataClassFromLib { myInt = 0 })
                .addDataClassSet(dataClassFromLib { myInt = 0 })
                .build()
        )
    }

    @Test
    fun dataClassCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = DataClassCollections(),
            protocInstance = DataClassCollectionsProto.DataClassCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun nullableDataClassCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = NullableDataClassCollections(
                nullableDataClassList = listOf(DataClassFromLib(10), DataClassFromLib(20)),
                nullableDataClassSet = setOf(DataClassFromLib(30), DataClassFromLib(40)),
            ),
            protocInstance = NullableDataClassCollectionsProto.NullableDataClassCollections.newBuilder()
                .addNullableDataClassList(dataClassFromLib { myInt = 10 })
                .addNullableDataClassList(dataClassFromLib { myInt = 20 })
                .setIsNullableDataClassListNull(NOT_NULL)
                .addNullableDataClassSet(dataClassFromLib { myInt = 30 })
                .addNullableDataClassSet(dataClassFromLib { myInt = 40 })
                .setIsNullableDataClassSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableDataClassCollections_null() {
        assertCompatibleSerialization(
            ktInstance = NullableDataClassCollections(
                nullableDataClassList = null,
                nullableDataClassSet = null,
            ),
            protocInstance = NullableDataClassCollectionsProto.NullableDataClassCollections.newBuilder()
                .setIsNullableDataClassListNull(NULL)
                .setIsNullableDataClassSetNull(NULL)
                .build()
        )
    }

    @Test
    fun nullableDataClassCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = NullableDataClassCollections(
                nullableDataClassList = listOf(DataClassFromLib(0)),
                nullableDataClassSet = setOf(DataClassFromLib(0)),
            ),
            protocInstance = NullableDataClassCollectionsProto.NullableDataClassCollections.newBuilder()
                .addNullableDataClassList(dataClassFromLib { myInt = 0 })
                .setIsNullableDataClassListNull(NOT_NULL)
                .addNullableDataClassSet(dataClassFromLib { myInt = 0 })
                .setIsNullableDataClassSetNull(NOT_NULL)
                .build()
        )
    }
}
