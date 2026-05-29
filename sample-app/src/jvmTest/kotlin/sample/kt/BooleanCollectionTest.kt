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
        val original = BooleanCollections(booleanList = listOf(true, true, true))
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanList_allFalse() {
        val original = BooleanCollections(booleanList = listOf(false, false, false))
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanList_mixed() {
        val original = BooleanCollections(booleanList = listOf(true, false, true, false))
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanSet_trueAndFalse() {
        val original = BooleanCollections(booleanSet = setOf(true, false))
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanSet_onlyFalse() {
        val original = BooleanCollections(booleanSet = setOf(false))
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanList_empty() {
        val original = BooleanCollections()
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanList_singleFalse() {
        val original = BooleanCollections(booleanList = listOf(false))
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun booleanList_singleTrue() {
        val original = BooleanCollections(booleanList = listOf(true))
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun nullableBooleanList_withValues() {
        val original = BooleanCollections(nullableBooleanList = listOf(true, false, true))
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun nullableBooleanList_null() {
        val original = BooleanCollections(nullableBooleanList = null)
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun nullableBooleanSet_withValues() {
        val original = BooleanCollections(nullableBooleanSet = setOf(true, false))
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun nullableBooleanSet_null() {
        val original = BooleanCollections(nullableBooleanSet = null)
        val encoded = serializer.encodeToByteArray(original)
        val decoded = serializer.decodeFromByteArray<BooleanCollections>(encoded)
        assertEquals(original, decoded)
    }
}
