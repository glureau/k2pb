package sample.kt

import com.glureau.k2pb.runtime.decodeFromByteArray
import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.sample.BooleanCollections
import org.junit.Assert.assertEquals
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

/**
 * Tests for List<Boolean> and Set<Boolean> encoding/decoding.
 * These tests target BUG-2 which causes false values in collections to be encoded as true.
 */
class BooleanCollectionTest : BaseEncodingTest() {

    @Test
    fun booleanList_allTrue() {
        val original = BooleanCollections(
            booleanList = listOf(true, true, true),
            booleanSet = emptySet(),
        )
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanList_allFalse() {
        val original = BooleanCollections(
            booleanList = listOf(false, false, false),
            booleanSet = emptySet(),
        )
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanList_mixed() {
        val original = BooleanCollections(
            booleanList = listOf(true, false, true, false),
            booleanSet = emptySet(),
        )
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanSet_trueAndFalse() {
        val original = BooleanCollections(
            booleanList = emptyList(),
            booleanSet = setOf(true, false),
        )
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanSet_onlyFalse() {
        val original = BooleanCollections(
            booleanList = emptyList(),
            booleanSet = setOf(false),
        )
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanList_empty() {
        val original = BooleanCollections(
            booleanList = emptyList(),
            booleanSet = emptySet(),
        )
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanList_singleFalse() {
        val original = BooleanCollections(
            booleanList = listOf(false),
            booleanSet = emptySet(),
        )
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanList_singleTrue() {
        val original = BooleanCollections(
            booleanList = listOf(true),
            booleanSet = emptySet(),
        )
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }
}
