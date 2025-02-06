package sample.kt

import com.glureau.k2pb.ProtoConstructor
import com.glureau.k2pb.runtime.decodeFromByteArray
import com.glureau.k2pb.runtime.encodeToByteArray
import com.glureau.sample.MigrationAddFieldAfter
import com.glureau.sample.MigrationAddFieldAfterNullable
import com.glureau.sample.MigrationAddFieldBefore
import com.glureau.sample.MigrationData
import sample.serializer
import kotlin.test.Test
import kotlin.test.assertEquals

class MigrationTest {

    @OptIn(ExperimentalStdlibApi::class)
    inline fun <reified Old : Any, reified New : Any> assertMigration(
        old: Old,
        expected: New,
    ) {
        val encoded: ByteArray = serializer.encodeToByteArray<Old>(old)
        println("encoded : ${encoded.joinToString(" ") { it.toHexString() }}")
        val decoded = serializer.decodeFromByteArray<New>(encoded)
        println("decoded : $decoded")
        println("expected: $expected")
        assertEquals(decoded, expected)
    }

    @Test
    fun `add a field`() {
        assertMigration(
            MigrationAddFieldBefore("a", "b"),
            MigrationAddFieldAfterNullable("a", "b", 0, "")
        )
        assertMigration(
            MigrationAddFieldBefore("a", "b"),
            MigrationAddFieldAfter("a", "b", 33, "hardcoded in migration", MigrationData("hardcoded here too"))
        )
    }
}

// Generated
interface MigrationAddFieldAfterProtoConstructor {
    public operator fun invoke(
        a: String?,
        b: String?,
        c: Int?,
        d: String?,
        e: MigrationData?
    ) = MigrationAddFieldAfter(
        a = requireNotNull(a),
        b = requireNotNull(b),
        c = requireNotNull(c),
        d = requireNotNull(d),
        e = requireNotNull(e),
    )
}

fun main() {
    val res: MigrationAddFieldAfter = object : MigrationAddFieldAfterProtoConstructor {}(null, null, null, null, null)
    // OR
    val reS: MigrationAddFieldAfter = MigrationAddFieldAfterCtor(null, null, null, null, null)
}

object MigrationAddFieldAfter : ProtoConstructor<MigrationAddFieldAfter> {
    override fun create(parameters: Map<String, Any?>): MigrationAddFieldAfter =
        MigrationAddFieldAfter(
            a = parameters["a"] as String,
            b = parameters["b"] as String,
            c = 33,
            d = "hardcoded here too",
            e = MigrationData("hardcoded in migration")
        )
}

object MigrationAddFieldAfterCtor : MigrationAddFieldAfterProtoConstructor {
    override fun invoke(
        a: String?,
        b: String?,
        c: Int?,
        d: String?,
        e: MigrationData?,
    ) =
        MigrationAddFieldAfter(
            a = requireNotNull(a),
            b = requireNotNull(b),
            c = b.length,
            d = "hardcoded  in migration",
            e = MigrationData("hardcoded here too")
        )
}