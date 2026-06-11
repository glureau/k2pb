package sample.kt.collection

import com.glureau.custom.javapackage.AnEnumProto
import com.glureau.k2pb_sample.EnumCollectionsProto
import com.glureau.sample.collection.EnumCollections
import com.glureau.sample.lib.AnEnum
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class EnumCollectionTest : BaseEncodingTest() {

    @Test
    fun enumCollections_withValues() {
        assertCompatibleSerialization(
            ktInstance = EnumCollections(
                enumList = listOf(AnEnum.AnEnum_B, AnEnum.AnEnum_C),
                enumSet = setOf(AnEnum.AnEnum_B, AnEnum.AnEnum_C),
            ),
            protocInstance = EnumCollectionsProto.EnumCollections.newBuilder()
                .addEnumList(AnEnumProto.AnEnum.AnEnum_B)
                .addEnumList(AnEnumProto.AnEnum.AnEnum_C)
                .addEnumSet(AnEnumProto.AnEnum.AnEnum_B)
                .addEnumSet(AnEnumProto.AnEnum.AnEnum_C)
                .build()
        )
    }

    @Test
    fun enumCollections_withDefaultEnumValue() {
        assertCompatibleSerialization(
            ktInstance = EnumCollections(
                enumList = listOf(AnEnum.AnEnum_A, AnEnum.AnEnum_B, AnEnum.AnEnum_A),
                enumSet = setOf(AnEnum.AnEnum_A),
            ),
            protocInstance = EnumCollectionsProto.EnumCollections.newBuilder()
                .addEnumList(AnEnumProto.AnEnum.AnEnum_A)
                .addEnumList(AnEnumProto.AnEnum.AnEnum_B)
                .addEnumList(AnEnumProto.AnEnum.AnEnum_A)
                .addEnumSet(AnEnumProto.AnEnum.AnEnum_A)
                .build()
        )
    }

    @Test
    fun enumCollections_empty() {
        assertCompatibleSerialization(
            ktInstance = EnumCollections(),
            protocInstance = EnumCollectionsProto.EnumCollections.newBuilder()
                .build()
        )
    }

    @Test
    fun enumCollections_allDefaultValues() {
        assertCompatibleSerialization(
            ktInstance = EnumCollections(
                enumList = listOf(AnEnum.AnEnum_A, AnEnum.AnEnum_A),
                enumSet = setOf(AnEnum.AnEnum_A),
            ),
            protocInstance = EnumCollectionsProto.EnumCollections.newBuilder()
                .addEnumList(AnEnumProto.AnEnum.AnEnum_A)
                .addEnumList(AnEnumProto.AnEnum.AnEnum_A)
                .addEnumSet(AnEnumProto.AnEnum.AnEnum_A)
                .build()
        )
    }
}
