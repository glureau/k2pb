package sample.kt.collection

import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.NullableStringCollectionsProto
import com.glureau.k2pb_sample.StringCollectionsProto
import com.glureau.sample.collection.NullableStringCollections
import com.glureau.sample.collection.StringCollections
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class StringCollectionTest : BaseEncodingTest() {

    @Test
    fun stringCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = StringCollections(
                stringList = listOf("aaa", "bbb", "ccc"),
                stringSet = setOf("zzz", "xxx", "yyy"),
            ),
            protocInstance = StringCollectionsProto.StringCollections.newBuilder()
                .addStringList("aaa")
                .addStringList("bbb")
                .addStringList("ccc")
                .addStringSet("zzz")
                .addStringSet("xxx")
                .addStringSet("yyy")
                .build()
        )
    }

    @Test
    fun stringCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = StringCollections(
                stringList = listOf("", "", ""),
                stringSet = setOf(""),
            ),
            protocInstance = StringCollectionsProto.StringCollections.newBuilder()
                .addStringList("")
                .addStringList("")
                .addStringList("")
                .addStringSet("")
                .build()
        )
    }

    @Test
    fun stringCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = StringCollections(),
            protocInstance = StringCollectionsProto.StringCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun nullableStringCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = NullableStringCollections(
                nullableStringList = listOf("ddd", "eee"),
                nullableStringSet = setOf("fff"),
            ),
            protocInstance = NullableStringCollectionsProto.NullableStringCollections.newBuilder()
                .addNullableStringList("ddd")
                .addNullableStringList("eee")
                .setIsNullableStringListNull(NOT_NULL)
                .addNullableStringSet("fff")
                .setIsNullableStringSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableStringCollections_null() {
        assertCompatibleSerialization(
            ktInstance = NullableStringCollections(
                nullableStringList = null,
                nullableStringSet = null,
            ),
            protocInstance = NullableStringCollectionsProto.NullableStringCollections.newBuilder()
                .setIsNullableStringListNull(NULL)
                .setIsNullableStringSetNull(NULL)
                .build()
        )
    }

    @Test
    fun nullableStringCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = NullableStringCollections(
                nullableStringList = listOf("", ""),
                nullableStringSet = setOf(""),
            ),
            protocInstance = NullableStringCollectionsProto.NullableStringCollections.newBuilder()
                .addNullableStringList("")
                .addNullableStringList("")
                .setIsNullableStringListNull(NOT_NULL)
                .addNullableStringSet("")
                .setIsNullableStringSetNull(NOT_NULL)
                .build()
        )
    }
}
