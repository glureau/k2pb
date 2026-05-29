package sample.kt.collection

import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NOT_NULL
import com.glureau.k2pb.K2PBConstants.ExplicitNullability.NULL
import com.glureau.k2pb_sample.CharCollectionsProto
import com.glureau.k2pb_sample.NullableCharCollectionsProto
import com.glureau.sample.collection.CharCollections
import com.glureau.sample.collection.NullableCharCollections
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class CharCollectionTest : BaseEncodingTest() {

    @Test
    fun charCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = CharCollections(
                charList = listOf('a', 'b', 'z'),
                charSet = setOf('x', 'y', 'z'),
            ),
            protocInstance = CharCollectionsProto.CharCollections.newBuilder()
                .addCharList('a'.code)
                .addCharList('b'.code)
                .addCharList('z'.code)
                .addCharSet('x'.code)
                .addCharSet('y'.code)
                .addCharSet('z'.code)
                .build()
        )
    }

    @Test
    fun charCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = CharCollections(
                charList = listOf('\u0000', '\u0000'),
                charSet = setOf('\u0000'),
            ),
            protocInstance = CharCollectionsProto.CharCollections.newBuilder()
                .addCharList(0)
                .addCharList(0)
                .addCharSet(0)
                .build()
        )
    }

    @Test
    fun charCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = CharCollections(),
            protocInstance = CharCollectionsProto.CharCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun nullableCharCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = NullableCharCollections(
                nullableCharList = listOf('A', 'B'),
                nullableCharSet = setOf('C', 'D'),
            ),
            protocInstance = NullableCharCollectionsProto.NullableCharCollections.newBuilder()
                .addNullableCharList('A'.code)
                .addNullableCharList('B'.code)
                .setIsNullableCharListNull(NOT_NULL)
                .addNullableCharSet('C'.code)
                .addNullableCharSet('D'.code)
                .setIsNullableCharSetNull(NOT_NULL)
                .build()
        )
    }

    @Test
    fun nullableCharCollections_null() {
        assertCompatibleSerialization(
            ktInstance = NullableCharCollections(
                nullableCharList = null,
                nullableCharSet = null,
            ),
            protocInstance = NullableCharCollectionsProto.NullableCharCollections.newBuilder()
                .setIsNullableCharListNull(NULL)
                .setIsNullableCharSetNull(NULL)
                .build()
        )
    }

    @Test
    fun nullableCharCollections_defaults() {
        assertCompatibleSerialization(
            ktInstance = NullableCharCollections(
                nullableCharList = listOf('\u0000', '\u0000'),
                nullableCharSet = setOf('\u0000'),
            ),
            protocInstance = NullableCharCollectionsProto.NullableCharCollections.newBuilder()
                .addNullableCharList(0)
                .addNullableCharList(0)
                .setIsNullableCharListNull(NOT_NULL)
                .addNullableCharSet(0)
                .setIsNullableCharSetNull(NOT_NULL)
                .build()
        )
    }
}
