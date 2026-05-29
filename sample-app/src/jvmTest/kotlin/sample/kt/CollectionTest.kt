package sample.kt

import com.glureau.custom.javapackage.AnEnumProto
import com.glureau.custom.javapackage.dataClassFromLib
import com.glureau.custom.javapackage.valueClassList
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb_sample.DataClassCollectionsProto
import com.glureau.k2pb_sample.InlinedCollectionProto
import com.glureau.k2pb_sample.IntCollectionsProto
import com.glureau.k2pb_sample.StringCollectionsProto
import com.glureau.sample.DataClassCollections
import com.glureau.sample.InlinedCollection
import com.glureau.sample.IntCollections
import com.glureau.sample.StringCollections
import com.glureau.sample.lib.AnEnum
import com.glureau.sample.lib.DataClassFromLib
import com.glureau.sample.lib.NullableValueClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import com.glureau.sample.lib.ValueClassList
import com.glureau.sample.lib.ValueClassOfEnum
import com.glureau.sample.lib.ValueClassOfNullableEnum
import org.junit.Test
import sample.kt.tools.BaseEncodingTest
import kotlin.test.Ignore

class CollectionTest : BaseEncodingTest() {

    @Test
    fun listOfValueClass() {
        assertCompatibleSerialization(
            ktInstance = ValueClassList(listOf(ValueClassFromLib("42"), ValueClassFromLib("43"))),
            protocInstance = valueClassList {
                valueClassFromLibs += "42"
                valueClassFromLibs += "43"
            }
        )
    }

    @Test
    fun checkIntCollections() {
        assertCompatibleSerialization(
            ktInstance = IntCollections(
                integerList = listOf(1, 3, 5),
                integerSet = setOf(100, 1, 3),
                nullableIntegerList = listOf(42, 51),
                nullableIntegerSet = setOf(39, 38),
            ),
            protocInstance = IntCollectionsProto.IntCollections.newBuilder()
                .addIntegerList(1)
                .addIntegerList(3)
                .addIntegerList(5)
                .addIntegerSet(100)
                .addIntegerSet(1)
                .addIntegerSet(3)
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
    fun checkIntCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = IntCollections(
                integerList = listOf(0, 0, 0),
                integerSet = setOf(0),
                nullableIntegerList = listOf(0, 0),
                nullableIntegerSet = setOf(0),
            ),
            protocInstance = IntCollectionsProto.IntCollections.newBuilder()
                .addIntegerList(0)
                .addIntegerList(0)
                .addIntegerList(0)
                .addIntegerSet(0)
                .addNullableIntegerList(0)
                .addNullableIntegerList(0)
                .setIsNullableIntegerListNull(NOT_NULL)
                .addNullableIntegerSet(0)
                .setIsNullableIntegerSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun checkStringCollections() {
        assertCompatibleSerialization(
            ktInstance = StringCollections(
                stringList = listOf("aaa", "bbb", "ccc"),
                stringSet = setOf("zzz", "xxx", "yyy"),
                nullableStringList = listOf("ddd", "eee"),
                nullableStringSet = setOf("fff"),
            ),
            protocInstance = StringCollectionsProto.StringCollections.newBuilder()
                .addStringList("aaa")
                .addStringList("bbb")
                .addStringList("ccc")
                .addStringSet("zzz")
                .addStringSet("xxx")
                .addStringSet("yyy")
                .addNullableStringList("ddd")
                .addNullableStringList("eee")
                .setIsNullableStringListNull(NOT_NULL)
                .addNullableStringSet("fff")
                .setIsNullableStringSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun checkStringCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = StringCollections(
                stringList = listOf("", "", ""),
                stringSet = setOf(""),
                nullableStringList = listOf("", ""),
                nullableStringSet = setOf(""),
            ),
            protocInstance = StringCollectionsProto.StringCollections.newBuilder()
                .addStringList("")
                .addStringList("")
                .addStringList("")
                .addStringSet("")
                .addNullableStringList("")
                .addNullableStringList("")
                .setIsNullableStringListNull(NOT_NULL)
                .addNullableStringSet("")
                .setIsNullableStringSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun checkDataClassCollections() {
        assertCompatibleSerialization(
            ktInstance = DataClassCollections(
                dataClassList = listOf(DataClassFromLib(33), DataClassFromLib(34)),
                dataClassSet = setOf(DataClassFromLib(37), DataClassFromLib(36)),
                mapStringInt = mapOf("a" to 2, "b" to 4),
                mapStringObject = mapOf("d" to DataClassFromLib(68)),
            ),
            protocInstance = DataClassCollectionsProto.DataClassCollections.newBuilder()
                .addDataClassList(dataClassFromLib { myInt = 33 })
                .addDataClassList(dataClassFromLib { myInt = 34 })
                .addDataClassSet(dataClassFromLib { myInt = 37 })
                .addDataClassSet(dataClassFromLib { myInt = 36 })
                .putMapStringInt("a", 2)
                .putMapStringInt("b", 4)
                .putMapStringObject("d", dataClassFromLib { myInt = 68 })
                .build()
        )
    }

    @Test
    fun checkDataClassCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = DataClassCollections(
                dataClassList = listOf(DataClassFromLib(0)),
                dataClassSet = setOf(DataClassFromLib(0)),
                mapStringInt = mapOf("" to 0),
                mapStringObject = mapOf("" to DataClassFromLib(0)),
            ),
            protocInstance = DataClassCollectionsProto.DataClassCollections.newBuilder()
                .addDataClassList(dataClassFromLib { myInt = 0 })
                .addDataClassSet(dataClassFromLib { myInt = 0 })
                .putMapStringInt("", 0)
                .putMapStringObject("", dataClassFromLib { myInt = 0 })
                .build()
        )
    }

    @Ignore // Not fully supported yet
    @Test
    fun checkInlinedCollection() {
        assertCompatibleSerialization(
            ktInstance = InlinedCollection(
                valueClassList = listOf(
                    ValueClassFromLib(""),
                    ValueClassFromLib("go"),
                ),
                valueClassOfEnumList = listOf(
                    ValueClassOfEnum(AnEnum.AnEnum_A),
                    ValueClassOfEnum(AnEnum.AnEnum_B),
                ),
                valueClassOfNullableEnumList = listOf(
                    ValueClassOfNullableEnum(AnEnum.AnEnum_A),
                    //ValueClassOfNullableEnum(null),
                    ValueClassOfNullableEnum(AnEnum.AnEnum_B),
                ),
                valueClassOfNullableStringList = listOf(
                    NullableValueClassFromLib("a"),
                    //NullableValueClassFromLib(null),
                    NullableValueClassFromLib(""),
                    NullableValueClassFromLib("b"),
                ),
                valueClassSet = setOf(
                    ValueClassFromLib(""),
                    ValueClassFromLib(""),
                ),
                valueClassOfEnumSet = setOf(
                    ValueClassOfEnum(AnEnum.AnEnum_A),
                    ValueClassOfEnum(AnEnum.AnEnum_B),
                ),
                valueClassOfNullableEnumSet = setOf(
                    ValueClassOfNullableEnum(AnEnum.AnEnum_A),
                    //ValueClassOfNullableEnum(null),
                    ValueClassOfNullableEnum(AnEnum.AnEnum_B),
                ),
                valueClassOfNullableStringSet = setOf(
                    NullableValueClassFromLib("a"),
                    //NullableValueClassFromLib(null),
                    NullableValueClassFromLib(""),
                    NullableValueClassFromLib("b"),
                ),
            ),
            protocInstance = InlinedCollectionProto.InlinedCollection.newBuilder()
                .addValueClassList("")
                .addValueClassList("go")
                .addValueClassOfEnumList(AnEnumProto.AnEnum.AnEnum_A)
                .addValueClassOfEnumList(AnEnumProto.AnEnum.AnEnum_B)
                .addValueClassOfNullableEnumList(AnEnumProto.AnEnum.AnEnum_A)
                //.addValueClassOfNullableEnumList(null)
                .addValueClassOfNullableEnumList(AnEnumProto.AnEnum.AnEnum_B)
                .addValueClassOfNullableStringList("a")
                //.addValueClassOfNullableStringList(null)
                .addValueClassOfNullableStringList("")
                .addValueClassOfNullableStringList("b")
                .build()
        )
    }

}