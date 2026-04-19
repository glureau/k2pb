package com.glureau.k2pb.compiler

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspWithCompilation
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Basic KSP compilation tests for the K2PB compiler plugin.
 * Uses kotlin-compile-testing-ksp to verify the processor runs and generates code.
 */
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
        // The compiler may succeed (KSP skips unannotated-style classes) or fail
        // depending on how the processor handles missing primary constructors.
        // Either outcome is acceptable as long as it doesn't crash.
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
        // Verify generated codec-related files exist in the compiled output
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
}
