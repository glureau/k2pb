package sample.kt

import com.glureau.custom.javapackage.AnEnumProto
import com.glureau.custom.javapackage.EnumHolderProto
import com.glureau.custom.javapackage.ValueClassOfEnumHolderProto
import com.glureau.k2pb_sample.NonContiguousEnumHolderProto
import com.glureau.k2pb_sample.NonContiguousEnumProto
import com.glureau.sample.NonContiguousEnum
import com.glureau.sample.NonContiguousEnumHolder
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
            protocInstance = EnumHolderProto.EnumHolder.newBuilder()
                .setValue(AnEnumProto.AnEnum.AnEnum_A)
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = EnumHolder(AnEnum.AnEnum_B),
            protocInstance = EnumHolderProto.EnumHolder.newBuilder()
                .setValue(AnEnumProto.AnEnum.AnEnum_B)
                .build()
        )
    }

    @Test
    fun valueClassOfEnum() {
        assertCompatibleSerialization(
            ktInstance = ValueClassOfEnumHolder(ValueClassOfEnum(AnEnum.AnEnum_A)), // Default value, not encoded
            protocInstance = ValueClassOfEnumHolderProto.ValueClassOfEnumHolder.newBuilder()
                .setValue(AnEnumProto.AnEnum.AnEnum_A)
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = ValueClassOfEnumHolder(ValueClassOfEnum(AnEnum.AnEnum_B)),
            protocInstance = ValueClassOfEnumHolderProto.ValueClassOfEnumHolder.newBuilder()
                .setValue(AnEnumProto.AnEnum.AnEnum_B)
                .build()
        )
    }

    @Test
    fun nonContiguousEnum_allValues() {
        assertCompatibleSerialization(
            ktInstance = NonContiguousEnumHolder(NonContiguousEnum.UNKNOWN),
            protocInstance = NonContiguousEnumHolderProto.NonContiguousEnumHolder.newBuilder()
                .setStatus(NonContiguousEnumProto.NonContiguousEnum.UNKNOWN)
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = NonContiguousEnumHolder(NonContiguousEnum.ACTIVE),
            protocInstance = NonContiguousEnumHolderProto.NonContiguousEnumHolder.newBuilder()
                .setStatus(NonContiguousEnumProto.NonContiguousEnum.ACTIVE)
                .build()
        )
        assertCompatibleSerialization(
            ktInstance = NonContiguousEnumHolder(NonContiguousEnum.INACTIVE),
            protocInstance = NonContiguousEnumHolderProto.NonContiguousEnumHolder.newBuilder()
                .setStatus(NonContiguousEnumProto.NonContiguousEnum.INACTIVE)
                .build()
        )
    }
}