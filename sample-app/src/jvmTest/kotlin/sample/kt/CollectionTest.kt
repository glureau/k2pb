package sample.kt

import com.glureau.custom.javapackage.AnEnumProto
import com.glureau.custom.javapackage.dataClassFromLib
import com.glureau.custom.javapackage.valueClassList
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb_sample.CollectionTypeProto
import com.glureau.k2pb_sample.InlinedCollectionProto
import com.glureau.sample.CollectionType
import com.glureau.sample.InlinedCollection
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
    fun checkCollectionTypeEvent() {
        assertCompatibleSerialization(
            ktInstance = CollectionType(
                integerList = listOf(1, 3, 5),
                stringList = listOf("aaa", "bbb", "ccc"),
                maybeIntegerList = listOf(42, 51),
                mapStringInt = mapOf(
                    "a" to 2,
                    "b" to 4,
                ),
                dataClassList = listOf(DataClassFromLib(33), DataClassFromLib(34)),
                mapStringObject = mapOf(
                    "d" to DataClassFromLib(68)
                ),
                integerSet = setOf(100, 1, 3),
                stringSet = setOf("zzz", "xxx", "yyy"),
                maybeIntegerSet = setOf(39, 38),
                dataClassSet = setOf(DataClassFromLib(37), DataClassFromLib(36)),
            ),
            protocInstance = CollectionTypeProto.CollectionType.newBuilder()
                // Randomized order => preserve proto number sorting in serialization
                .addIntegerList(1)
                .addDataClassList(
                    dataClassFromLib {
                        myInt = 33
                    }
                )
                .addIntegerList(3)
                .addDataClassList(
                    dataClassFromLib {
                        myInt = 34
                    }
                )
                .putMapStringInt("a", 2)
                .addIntegerList(5)
                .putMapStringInt("b", 4)
                .addMaybeIntegerList(42)
                .addMaybeIntegerList(51)
                .setIsMaybeIntegerListNull(NOT_NULL)
                .addStringList("aaa")
                .addStringList("bbb")
                .addStringList("ccc")
                .putMapStringObject("d", dataClassFromLib { myInt = 68 })
                .addIntegerSet(100)
                .addIntegerSet(1)
                .addIntegerSet(3)
                .addStringSet("zzz")
                .addStringSet("xxx")
                .addStringSet("yyy")
                .addMaybeIntegerSet(39)
                .addMaybeIntegerSet(38)
                .setIsMaybeIntegerSetNull(NOT_NULL)
                .addDataClassSet(dataClassFromLib { myInt = 37 })
                .addDataClassSet(dataClassFromLib { myInt = 36 })
                .build()
        )
    }


    @Test
    fun defaultScalarAreStillEncoded() {
        assertCompatibleSerialization(
            ktInstance = CollectionType(
                integerList = listOf(0, 0, 0),
                stringList = listOf("", "", ""),
                maybeIntegerList = listOf(0, 0),
                mapStringInt = mapOf(
                    "" to 0,
                ),
                dataClassList = listOf(DataClassFromLib(0)),
                mapStringObject = mapOf(
                    "" to DataClassFromLib(0)
                ),
                integerSet = setOf(0, 0, 0), // That's a Set, only 1 '0' can exist
                stringSet = setOf("", ""), // That's a Set, only 1 "" can exist
                maybeIntegerSet = setOf(0),
                dataClassSet = setOf(DataClassFromLib(0)),
            ),
            protocInstance = CollectionTypeProto.CollectionType.newBuilder()
                .addIntegerList(0)
                .addIntegerList(0)
                .addIntegerList(0)
                .addStringList("")
                .addStringList("")
                .addStringList("")
                .addMaybeIntegerList(0)
                .addMaybeIntegerList(0)
                .setIsMaybeIntegerListNull(NOT_NULL)
                .putMapStringInt("", 0)
                .addDataClassList(
                    dataClassFromLib {
                        myInt = 0
                    }
                )
                .putMapStringObject("", dataClassFromLib { myInt = 0 })
                .addIntegerSet(0)
                .addStringSet("")
                .addMaybeIntegerSet(0)
                .setIsMaybeIntegerSetNull(NOT_NULL)
                .addDataClassSet(dataClassFromLib { myInt = 0 })
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