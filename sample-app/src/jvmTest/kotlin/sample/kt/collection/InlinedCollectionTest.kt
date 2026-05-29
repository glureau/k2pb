package sample.kt.collection

import com.glureau.custom.javapackage.AnEnumProto
import com.glureau.custom.javapackage.valueClassList
import com.glureau.k2pb_sample.InlinedCollectionProto
import com.glureau.sample.collection.InlinedCollection
import com.glureau.sample.lib.AnEnum
import com.glureau.sample.lib.NullableValueClassFromLib
import com.glureau.sample.lib.ValueClassFromLib
import com.glureau.sample.lib.ValueClassList
import com.glureau.sample.lib.ValueClassOfEnum
import com.glureau.sample.lib.ValueClassOfNullableEnum
import org.junit.Test
import sample.kt.tools.BaseEncodingTest
import kotlin.test.Ignore

class InlinedCollectionTest : BaseEncodingTest() {

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
