package sample.kt

import com.glureau.sample.CommonClass
import com.glureau.sample.NativeTypeEvent
import com.glureau.sample.NativeTypeEventUnspecifiedDefault
import com.glureau.sample.NullableEnumHolderUnspecifiedDefault
import com.glureau.sample.NullableEnumHolderUnspecifiedNull
import com.glureau.sample.NullableNativeTypeEventUnspecifiedDefault
import com.glureau.sample.NullableNativeTypeEventUnspecifiedNull
import com.glureau.sample.ObjectClass
import com.glureau.sample.OptionalToRequiredEnd
import com.glureau.sample.OptionalToRequiredEnumEnd
import com.glureau.sample.OptionalToRequiredEnumStart
import com.glureau.sample.OptionalToRequiredStart
import com.glureau.sample.RequiredToOptionalEnd
import com.glureau.sample.RequiredToOptionalEnumEnd
import com.glureau.sample.RequiredToOptionalEnumStart
import com.glureau.sample.RequiredToOptionalStart
import com.glureau.sample.lib.AnEnum
import org.junit.Test
import org.junit.jupiter.api.fail
import sample.kt.tools.BaseEncodingTest
import kotlin.test.assertEquals

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

    @Test
    fun `migration from optional to required enum with custom nullability proto number`() {
        assertMigration(
            before = OptionalToRequiredEnumStart(
                enum = AnEnum.AnEnum_B,
                b = "test"
            ),
            expectedAfter = OptionalToRequiredEnumEnd(
                enum = AnEnum.AnEnum_B,
                b = "test"
            )
        )
        assertMigration(
            before = OptionalToRequiredEnumStart(
                enum = null,
                b = "test"
            ),
            expectedAfter = OptionalToRequiredEnumEnd(
                enum = AnEnum.AnEnum_A,
                b = "test"
            )
        )
        assertMigration(
            before = OptionalToRequiredEnumStart(
                enum = AnEnum.AnEnum_A, // Default value
                b = "test"
            ),
            expectedAfter = OptionalToRequiredEnumEnd(
                enum = AnEnum.AnEnum_A,
                b = "test"
            )
        )
    }

    @Test
    fun `migration from optional to required with custom nullability proto number`() {
        assertMigration(
            before = OptionalToRequiredStart(
                item = CommonClass("hey"),
                b = "test"
            ),
            expectedAfter = OptionalToRequiredEnd(
                item = CommonClass("hey"),
                b = "test",
            )
        )
        try {
            assertMigration(
                before = OptionalToRequiredStart(
                    item = null,
                    b = "test"
                ),
                expectedAfter = OptionalToRequiredEnd(
                    // CRASH, this can't be decoded in the current configuration,
                    // there's no data on that proto number, so we can't fabricate a value.
                    // It's still possible to use a custom converter on OptionalToRequiredEnd but more complex.
                    item = CommonClass(""),
                    b = "test"
                )
            )
            fail { "This should have failed already" }
        } catch (t: IllegalArgumentException) {
            assertEquals("Field 'item' is declared as not nullable but is null", t.message)
        }
    }

    @Test
    fun `migration from required to optional enum with custom nullability proto number`() {
        assertMigration(
            before = RequiredToOptionalEnumStart(
                enum = AnEnum.AnEnum_B,
                b = "test"
            ),
            expectedAfter = RequiredToOptionalEnumEnd(
                enum = AnEnum.AnEnum_B,
                b = "test"
            )
        )
        assertMigration(
            before = RequiredToOptionalEnumStart(
                enum = AnEnum.AnEnum_A,
                b = "test"
            ),
            expectedAfter = RequiredToOptionalEnumEnd(
                enum = AnEnum.AnEnum_A, // Default value, cannot disambiguate a null from that message
                b = "test"
            )
        )

        // read-write is still ok
        assertMigration(
            before = RequiredToOptionalEnumStart(
                enum = AnEnum.AnEnum_B,
                b = "test2"
            ),
            expectedAfter = RequiredToOptionalEnumStart(
                enum = AnEnum.AnEnum_B,
                b = "test2"
            )
        )
        assertMigration(
            before = RequiredToOptionalEnumEnd(
                enum = null,
                b = "test3"
            ),
            expectedAfter = RequiredToOptionalEnumEnd(
                enum = null,
                b = "test3"
            )
        )
    }

    @Test
    fun `migration from required to optional with custom nullability proto number`() {
        assertMigration(
            before = RequiredToOptionalStart(
                item = CommonClass("hey"),
                b = "test"
            ),
            expectedAfter = RequiredToOptionalEnd(
                item = CommonClass("hey"),
                b = "test"
            )
        )
        assertMigration(
            before = RequiredToOptionalStart(
                item = CommonClass(""),
                b = "test"
            ),
            expectedAfter = RequiredToOptionalEnd(
                item = CommonClass(""), // Default value, cannot disambiguate a null from that message
                b = "test"
            )
        )

        // read-write is still ok
        assertMigration(
            before = RequiredToOptionalStart(
                item = CommonClass("hey"),
                b = "test2"
            ),
            expectedAfter = RequiredToOptionalStart(
                item = CommonClass("hey"),
                b = "test2"
            )
        )
        assertMigration(
            before = RequiredToOptionalEnd(
                item = null,
                b = "test3"
            ),
            expectedAfter = RequiredToOptionalEnd(
                item = null,
                b = "test3"
            )
        )
    }
}
