package sample.kt

import com.glureau.k2pb_sample.AnEnumOuterClass
import com.glureau.k2pb_sample.CollectionTypeOuterClass
import com.glureau.k2pb_sample.InlinedCollectionOuterClass
import com.glureau.k2pb_sample.dataClassFromLib
import com.glureau.k2pb_sample.valueClassList
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
            ),
            protocInstance = CollectionTypeOuterClass.CollectionType.newBuilder()
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
                .addStringList("aaa")
                .addStringList("bbb")
                .addStringList("ccc")
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
            ),
            protocInstance = CollectionTypeOuterClass.CollectionType.newBuilder()
                .addIntegerList(0)
                .addIntegerList(0)
                .addIntegerList(0)
                .addStringList("")
                .addStringList("")
                .addStringList("")
                .addMaybeIntegerList(0)
                .addMaybeIntegerList(0)
                .putMapStringInt("", 0)
                .addDataClassList(
                    dataClassFromLib {
                        myInt = 0
                    }
                )
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
            ),
            protocInstance = InlinedCollectionOuterClass.InlinedCollection.newBuilder()
                .addValueClassList("")
                .addValueClassList("go")
                .addValueClassOfEnumList(AnEnumOuterClass.AnEnum.AnEnum_A)
                .addValueClassOfEnumList(AnEnumOuterClass.AnEnum.AnEnum_B)
                .addValueClassOfNullableEnumList(AnEnumOuterClass.AnEnum.AnEnum_A)
                //.addValueClassOfNullableEnumList(null)
                .addValueClassOfNullableEnumList(AnEnumOuterClass.AnEnum.AnEnum_B)
                .addValueClassOfNullableStringList("a")
                //.addValueClassOfNullableStringList(null)
                .addValueClassOfNullableStringList("")
                .addValueClassOfNullableStringList("b")
                .build()
        )
    }

}