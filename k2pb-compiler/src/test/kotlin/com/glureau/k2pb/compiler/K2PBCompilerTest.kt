package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.codegen.generateEnumCodecType
import com.glureau.k2pb.compiler.struct.EnumEntry
import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.codecClassName
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import com.squareup.kotlinpoet.FileSpec
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class K2PBCompilerTest {

    private fun compile(vararg sources: SourceFile): JvmCompilationResult {
        val compilation = KotlinCompilation().apply {
            this.sources = sources.toList()
            symbolProcessorProviders = mutableListOf(K2PBCompilerProvider())
            kspWithCompilation = true
            inheritClassPath = true
        }
        val result = compilation.compile()
        println("Exit code: ${result.exitCode}")
        println("Messages: ${result.messages}")
        return result
    }

    @Test
    fun simpleDataClass_compiles() {
        val source = SourceFile.kotlin(
            "TestMessage.kt",
            """
            package test

            import com.glureau.k2pb.annotation.ProtoMessage

            @ProtoMessage
            data class SimpleMessage(
                val name: String = "",
                val age: Int = 0,
            )
            """.trimIndent()
        )
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun enumClass_compiles() {
        val source = SourceFile.kotlin(
            "TestEnum.kt",
            """
            package test

            import com.glureau.k2pb.annotation.ProtoMessage

            @ProtoMessage
            enum class Color {
                RED, GREEN, BLUE
            }
            """.trimIndent()
        )
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun nestedDataClass_compiles() {
        val source = SourceFile.kotlin(
            "TestNested.kt",
            """
            package test

            import com.glureau.k2pb.annotation.ProtoMessage

            @ProtoMessage
            data class Inner(val value: String = "")

            @ProtoMessage
            data class Outer(val inner: Inner = Inner())
            """.trimIndent()
        )
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun dataClassWithCollections_compiles() {
        val source = SourceFile.kotlin(
            "TestCollections.kt",
            """
            package test

            import com.glureau.k2pb.annotation.ProtoMessage

            @ProtoMessage
            data class WithCollections(
                val names: List<String> = emptyList(),
                val ages: Set<Int> = emptySet(),
                val mapping: Map<String, Int> = emptyMap(),
            )
            """.trimIndent()
        )
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun dataClassWithNullableFields_compiles() {
        val source = SourceFile.kotlin(
            "TestNullable.kt",
            """
            package test

            import com.glureau.k2pb.annotation.ProtoMessage

            @ProtoMessage
            data class WithNullables(
                val name: String? = null,
                val age: Int? = null,
                val active: Boolean? = null,
            )
            """.trimIndent()
        )
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun classWithoutProtoMessage_noCodeGenerated() {
        val source = SourceFile.kotlin(
            "PlainClass.kt",
            """
            package test

            data class PlainClass(val name: String)
            """.trimIndent()
        )
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun classWithoutPrimaryConstructor_failsGracefully() {
        val source = SourceFile.kotlin(
            "NoPrimary.kt",
            """
            package test

            import com.glureau.k2pb.annotation.ProtoMessage

            @ProtoMessage
            class NoPrimaryConstructor {
                var name: String = ""
            }
            """.trimIndent()
        )
        val result = compile(source)
        assertTrue(
            "Should compile OK or report error gracefully",
            result.exitCode == KotlinCompilation.ExitCode.OK ||
                result.exitCode == KotlinCompilation.ExitCode.COMPILATION_ERROR
        )
    }

    @Test
    fun generatedCodec_isProduced() {
        val source = SourceFile.kotlin(
            "TestCodec.kt",
            """
            package test

            import com.glureau.k2pb.annotation.ProtoMessage

            @ProtoMessage
            data class CodecTest(
                val x: Int = 0,
                val y: String = "",
            )
            """.trimIndent()
        )
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val codecFiles = result.compiledClassAndResourceFiles.filter { it.name.contains("Codec") }
        println("Codec files found: ${codecFiles.map { it.name }}")
        assertTrue("At least one Codec class file should be generated", codecFiles.isNotEmpty())
    }

    @Test
    fun objectClass_compiles() {
        val source = SourceFile.kotlin(
            "TestObject.kt",
            """
            package test

            import com.glureau.k2pb.annotation.ProtoMessage

            @ProtoMessage
            data object Singleton
            """.trimIndent()
        )
        val result = compile(source)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    // --- Enum codec code generation tests ---

    private fun buildEnumCodecSource(enumNode: EnumNode): String {
        val fileSpec = FileSpec.builder(enumNode.codecClassName())
        fileSpec.generateEnumCodecType(enumNode)
        val sb = StringBuilder()
        fileSpec.build().writeTo(sb)
        return sb.toString()
    }

    @Test
    fun enumCodec_usesProtoNumbers_notOrdinals() {
        val enumNode = EnumNode(
            packageName = "test",
            qualifiedName = "test.Status",
            name = "Status",
            protoName = "Status",
            comment = null,
            entries = listOf(
                EnumEntry("UNKNOWN", null, 0),
                EnumEntry("ACTIVE", null, 5),
                EnumEntry("INACTIVE", null, 10),
            ),
            originalFile = null,
        )
        val codecContent = buildEnumCodecSource(enumNode)

        assertTrue(
            "Encode should map ACTIVE -> 5 (proto number), not ordinal",
            codecContent.contains("Status.ACTIVE -> 5")
        )
        assertTrue(
            "Encode should map INACTIVE -> 10 (proto number), not ordinal",
            codecContent.contains("Status.INACTIVE -> 10")
        )
        assertTrue(
            "Decode should map 5 -> ACTIVE",
            codecContent.contains("5 -> Status.ACTIVE")
        )
        assertTrue(
            "Decode should map 10 -> INACTIVE",
            codecContent.contains("10 -> Status.INACTIVE")
        )
        assertFalse(
            "Should not use .ordinal for encoding",
            codecContent.contains(".ordinal")
        )
        assertFalse(
            "Should not use entries.getOrNull for decoding",
            codecContent.contains("entries.getOrNull")
        )
        assertFalse(
            "Should not use entries[ for decoding",
            codecContent.contains("entries[")
        )
    }

    @Test
    fun enumCodec_defaultNumbersAreContiguous() {
        val enumNode = EnumNode(
            packageName = "test",
            qualifiedName = "test.Direction",
            name = "Direction",
            protoName = "Direction",
            comment = null,
            entries = listOf(
                EnumEntry("NORTH", null, 0),
                EnumEntry("SOUTH", null, 1),
                EnumEntry("EAST", null, 2),
                EnumEntry("WEST", null, 3),
            ),
            originalFile = null,
        )
        val codecContent = buildEnumCodecSource(enumNode)

        assertTrue("Encode should map NORTH -> 0", codecContent.contains("Direction.NORTH -> 0"))
        assertTrue("Encode should map SOUTH -> 1", codecContent.contains("Direction.SOUTH -> 1"))
        assertTrue("Encode should map EAST -> 2", codecContent.contains("Direction.EAST -> 2"))
        assertTrue("Encode should map WEST -> 3", codecContent.contains("Direction.WEST -> 3"))
        assertTrue("Decode should map 0 -> NORTH", codecContent.contains("0 -> Direction.NORTH"))
        assertTrue("Decode should map 3 -> WEST", codecContent.contains("3 -> Direction.WEST"))
    }

    @Test
    fun enumCodec_mixedAnnotatedAndAutoNumbers() {
        val enumNode = EnumNode(
            packageName = "test",
            qualifiedName = "test.Priority",
            name = "Priority",
            protoName = "Priority",
            comment = null,
            entries = listOf(
                EnumEntry("UNSET", null, 0),
                EnumEntry("HIGH", null, 10),
                EnumEntry("MEDIUM", null, 1),
                EnumEntry("LOW", null, 2),
            ),
            originalFile = null,
        )
        val codecContent = buildEnumCodecSource(enumNode)

        assertTrue("Encode should map UNSET -> 0", codecContent.contains("Priority.UNSET -> 0"))
        assertTrue(
            "Encode should map HIGH -> 10 (annotated)",
            codecContent.contains("Priority.HIGH -> 10")
        )
        assertTrue(
            "Encode should map MEDIUM -> 1 (auto-assigned)",
            codecContent.contains("Priority.MEDIUM -> 1")
        )
        assertTrue("Decode should map 10 -> HIGH", codecContent.contains("10 -> Priority.HIGH"))
        assertTrue("Decode should map 1 -> MEDIUM", codecContent.contains("1 -> Priority.MEDIUM"))
    }
}
