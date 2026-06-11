package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.ImportResolver
import com.glureau.k2pb.compiler.struct.DeprecatedField
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.ScalarType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ComputeDeprecatedProtobufImportsTest {

    private val importResolver = ImportResolver { protobufName ->
        "${protobufName.substringBefore(".")}.proto"
    }

    private fun messageNode(
        protoName: String = "TestMessage",
        deprecatedFields: List<DeprecatedField> = emptyList(),
    ) = MessageNode(
        packageName = "test",
        qualifiedName = "test.$protoName",
        name = protoName,
        protoName = protoName,
        isPolymorphic = false,
        isSealed = false,
        explicitGenerationRequested = false,
        isInlineClass = false,
        superTypes = emptyList(),
        comment = null,
        fields = emptyList(),
        deprecatedFields = deprecatedFields,
        originalFile = null,
        sealedSubClasses = emptyList(),
    )

    private fun deprecatedField(
        protoName: String,
        protoNumber: Int,
        protoType: String,
        publishedInProto: Boolean = true,
    ) = DeprecatedField(
        protoName = protoName,
        protoNumber = protoNumber,
        protoType = protoType,
        deprecationReason = null,
        publishedInProto = publishedInProto,
        migrationDecoder = null,
        migrationTargetClass = null,
    )

    @Test
    fun scalarType_doesNotGenerateImport() {
        for (scalarType in ScalarType.entries) {
            val node = messageNode(
                deprecatedFields = listOf(
                    deprecatedField("oldField", 10, scalarType.name)
                )
            )
            val imports = computeDeprecatedProtobufImports(listOf(node), importResolver)
            assertTrue(
                "Scalar type '${scalarType.name}' should not generate an import, but got: $imports",
                imports.isEmpty()
            )
        }
    }

    @Test
    fun messageType_generatesImport() {
        val node = messageNode(
            deprecatedFields = listOf(
                deprecatedField("oldField", 10, "SomeMessage")
            )
        )
        val imports = computeDeprecatedProtobufImports(listOf(node), importResolver)
        assertEquals(listOf("SomeMessage.proto"), imports)
    }

    @Test
    fun qualifiedMessageType_generatesImport() {
        val node = messageNode(
            deprecatedFields = listOf(
                deprecatedField("oldField", 10, "com.example.SomeMessage")
            )
        )
        val imports = computeDeprecatedProtobufImports(listOf(node), importResolver)
        assertEquals(listOf("com.proto"), imports)
    }

    @Test
    fun unpublishedDeprecatedField_doesNotGenerateImport() {
        val node = messageNode(
            deprecatedFields = listOf(
                deprecatedField("oldField", 10, "SomeMessage", publishedInProto = false)
            )
        )
        val imports = computeDeprecatedProtobufImports(listOf(node), importResolver)
        assertTrue("Unpublished deprecated field should not generate import", imports.isEmpty())
    }

    @Test
    fun mixedScalarAndMessageTypes_onlyMessageGeneratesImport() {
        val node = messageNode(
            deprecatedFields = listOf(
                deprecatedField("oldInt", 10, "int32"),
                deprecatedField("oldString", 11, "string"),
                deprecatedField("oldRef", 12, "OtherMessage"),
                deprecatedField("oldBool", 13, "bool"),
            )
        )
        val imports = computeDeprecatedProtobufImports(listOf(node), importResolver)
        assertEquals(listOf("OtherMessage.proto"), imports)
    }

    @Test
    fun selfReference_isExcludedFromImports() {
        val node = messageNode(
            protoName = "SelfRef",
            deprecatedFields = listOf(
                deprecatedField("oldSelf", 10, "SelfRef")
            )
        )
        val imports = computeDeprecatedProtobufImports(listOf(node), importResolver)
        assertTrue("Self-referencing deprecated field should not generate import", imports.isEmpty())
    }
}
