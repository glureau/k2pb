package sample.kt

import com.glureau.sample.NativeTypeEvent
import com.glureau.sample.NativeTypeEventUnspecifiedDefault
import com.glureau.sample.NullableEnumHolderUnspecifiedDefault
import com.glureau.sample.NullableEnumHolderUnspecifiedNull
import com.glureau.sample.NullableNativeTypeEventUnspecifiedDefault
import com.glureau.sample.NullableNativeTypeEventUnspecifiedNull
import com.glureau.sample.ObjectClass
import com.glureau.sample.lib.AnEnum
import org.junit.Test
import sample.kt.tools.BaseEncodingTest

class MigrationTest : BaseEncodingTest() {

    @Test
    fun `migration when adding scalar nullable fields`() {
        assertMigration(
            before = ObjectClass, // Nothing was encoded in this v1
            expectedAfter = NullableNativeTypeEventUnspecifiedNull(
                // We get null in the v2
                integer = null,
                long = null,
                float = null,
                double = null,
                string = null,
                short = null,
                char = null,
                boolean = null,
                byte = null,
                byteArray = null,
            )
        )
    }


    @Test
    fun `migration when adding scalar non-null fields`() {
        assertMigration(
            before = ObjectClass, // Nothing was encoded in this v1
            expectedAfter = NativeTypeEventUnspecifiedDefault(
                // We get defaults in the v2
                integer = 0,
                long = 0L,
                float = 0f,
                double = 0.0,
                string = "",
                short = 0,
                char = 0.toChar(),
                boolean = false,
                byte = 0.toByte(),
                byteArray = "".toByteArray(),
            )
        )
    }

    @Test
    fun `migration when changing scalars from non-null to nullable fields`() {
        assertMigration(
            before = NativeTypeEvent(
                // Nothing was encoded in this v1 (as it's only defaults)
                integer = 0,
                long = 0L,
                float = 0f,
                double = 0.0,
                string = "",
                short = 0,
                char = 0.toChar(),
                boolean = false,
                byte = 0.toByte(),
                byteArray = "".toByteArray(),
            ),
            expectedAfter = NullableNativeTypeEventUnspecifiedDefault(
                // We get default values in the v2
                integer = 0,
                long = 0L,
                float = 0f,
                double = 0.0,
                string = "",
                short = 0,
                char = 0.toChar(),
                boolean = false,
                byte = 0.toByte(),
                byteArray = "".toByteArray(),
            )
        )
    }

    @Test
    fun `migration when adding nullable enum with null`() {
        assertMigration(
            before = ObjectClass, // Nothing was encoded in this v1
            expectedAfter = NullableEnumHolderUnspecifiedNull(
                // We get null in the v2
                enum = null,
            )
        )
    }

    @Test
    fun `migration when adding nullable enum with default`() {
        assertMigration(
            before = ObjectClass, // Nothing was encoded in this v1
            expectedAfter = NullableEnumHolderUnspecifiedDefault(
                // We get null in the v2
                enum = AnEnum.AnEnum_A,
            )
        )
    }

}