package sample.kt

import com.glureau.k2pb_sample.AnEnumOuterClass
import com.glureau.k2pb_sample.EnumHolderOuterClass
import com.glureau.k2pb_sample.ValueClassOfEnumHolderOuterClass
import com.glureau.sample.lib.AnEnum
import com.glureau.sample.lib.EnumHolder
import com.glureau.sample.lib.ValueClassOfEnum
import com.glureau.sample.lib.ValueClassOfEnumHolder
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class EnumTest : BaseEncodingTest() {

    @Test
    fun enumHolder() {
        assertCompatibleSerialization(
            ktInstance = EnumHolder(AnEnum.AnEnum_A), // Default value, not encoded
            protocInstance = EnumHolderOuterClass.EnumHolder.newBuilder()
                .setValue(AnEnumOuterClass.AnEnum.AnEnum_A)
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = EnumHolder(AnEnum.AnEnum_B),
            protocInstance = EnumHolderOuterClass.EnumHolder.newBuilder()
                .setValue(AnEnumOuterClass.AnEnum.AnEnum_B)
                .build()
        )
    }

    @Test
    fun valueClassOfEnum() {
        assertCompatibleSerialization(
            ktInstance = ValueClassOfEnumHolder(ValueClassOfEnum(AnEnum.AnEnum_A)), // Default value, not encoded
            protocInstance = ValueClassOfEnumHolderOuterClass.ValueClassOfEnumHolder.newBuilder()
                .setValue(AnEnumOuterClass.AnEnum.AnEnum_A)
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = ValueClassOfEnumHolder(ValueClassOfEnum(AnEnum.AnEnum_B)),
            protocInstance = ValueClassOfEnumHolderOuterClass.ValueClassOfEnumHolder.newBuilder()
                .setValue(AnEnumOuterClass.AnEnum.AnEnum_B)
                .build()
        )
    }
}