package sample.kt.collection

import com.glureau.custom.javapackage.AnEnumProto
import com.glureau.custom.javapackage.dataClassFromLib
import com.glureau.k2pb_sample.MapCollectionsProto
import com.glureau.sample.collection.MapCollections
import com.glureau.sample.lib.AnEnum
import com.glureau.sample.lib.DataClassFromLib
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class MapCollectionTest : BaseEncodingTest() {

    @Test
    fun mapCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = MapCollections(
                mapStringInt = mapOf("a" to 2, "b" to 4),
                mapStringObject = mapOf("d" to DataClassFromLib(68)),
            ),
            protocInstance = MapCollectionsProto.MapCollections.newBuilder()
                .putMapStringInt("a", 2)
                .putMapStringInt("b", 4)
                .putMapStringObject("d", dataClassFromLib { myInt = 68 })
                .build()
        )
    }

    @Test
    fun mapCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = MapCollections(
                mapStringInt = mapOf("" to 0),
                mapStringObject = mapOf("" to DataClassFromLib(0)),
            ),
            protocInstance = MapCollectionsProto.MapCollections.newBuilder()
                .putMapStringInt("", 0)
                .putMapStringObject("", dataClassFromLib { myInt = 0 })
                .build()
        )
    }

    @Test
    fun mapCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = MapCollections(),
            protocInstance = MapCollectionsProto.MapCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun mapCollections_booleanValues() {
        assertCompatibleSerialization(
            ktInstance = MapCollections(
                mapStringBoolean = mapOf("enabled" to true, "disabled" to false),
            ),
            protocInstance = MapCollectionsProto.MapCollections.newBuilder()
                .putMapStringBoolean("enabled", true)
                .putMapStringBoolean("disabled", false)
                .build()
        )
    }

    @Test
    fun mapCollections_booleanAllFalse() {
        assertCompatibleSerialization(
            ktInstance = MapCollections(
                mapStringBoolean = mapOf("a" to false, "b" to false),
            ),
            protocInstance = MapCollectionsProto.MapCollections.newBuilder()
                .putMapStringBoolean("a", false)
                .putMapStringBoolean("b", false)
                .build()
        )
    }

    @Test
    fun mapCollections_enumValues() {
        assertCompatibleSerialization(
            ktInstance = MapCollections(
                mapStringEnum = mapOf(
                    "x" to AnEnum.AnEnum_A,
                    "y" to AnEnum.AnEnum_B,
                ),
            ),
            protocInstance = MapCollectionsProto.MapCollections.newBuilder()
                .putMapStringEnum("x", AnEnumProto.AnEnum.AnEnum_A)
                .putMapStringEnum("y", AnEnumProto.AnEnum.AnEnum_B)
                .build()
        )
    }

    @Test
    fun mapCollections_enumValuesAllDefault() {
        assertCompatibleSerialization(
            ktInstance = MapCollections(
                mapStringEnum = mapOf(
                    "a" to AnEnum.AnEnum_A,
                    "b" to AnEnum.AnEnum_A,
                ),
            ),
            protocInstance = MapCollectionsProto.MapCollections.newBuilder()
                .putMapStringEnum("a", AnEnumProto.AnEnum.AnEnum_A)
                .putMapStringEnum("b", AnEnumProto.AnEnum.AnEnum_A)
                .build()
        )
    }

    @Test
    fun mapCollections_enumKeys() {
        assertCompatibleSerialization(
            ktInstance = MapCollections(
                mapEnumString = mapOf(
                    AnEnum.AnEnum_A to "first",
                    AnEnum.AnEnum_B to "second",
                ),
            ),
            protocInstance = MapCollectionsProto.MapCollections.newBuilder()
                .putMapEnumString(0, "first")
                .putMapEnumString(1, "second")
                .build()
        )
    }

    @Test
    fun mapCollections_enumKeysDefaultValue() {
        assertCompatibleSerialization(
            ktInstance = MapCollections(
                mapEnumString = mapOf(
                    AnEnum.AnEnum_A to "only-default-key",
                ),
            ),
            protocInstance = MapCollectionsProto.MapCollections.newBuilder()
                .putMapEnumString(0, "only-default-key")
                .build()
        )
    }
}
