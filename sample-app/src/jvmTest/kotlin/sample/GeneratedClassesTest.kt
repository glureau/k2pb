package sample

import org.approvaltests.Approvals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class GeneratedClassesTest {

    private val generatedKotlinDir = File("build/generated/ksp/jvm/jvmMain/kotlin/")
    private val sourceDir = File("src/commonMain/kotlin/")

    @Test
    fun `check all generated files`() {
        val resourcePath = "build/generated/ksp/jvm/jvmMain/kotlin/"
        val generatedDir = File(resourcePath)
        var allKotlinGenerated = ""
        generatedDir.onEachFile { file ->
            allKotlinGenerated += "File: " + file.absolutePath.substringAfter("$resourcePath/") + "\n" + file.readText() + "\n"
        }

        Approvals.verify(allKotlinGenerated)
    }

    @Test
    fun `every ProtoMessage class has a generated codec`() {
        val protoMessageRegex = Regex("""@ProtoMessage[^)]*\)?\s*(?:data\s+)?(?:sealed\s+)?(?:abstract\s+)?(?:value\s+)?(?:class|object|enum\s+class)\s+(\w+)""")
        val annotatedClasses = sourceDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .flatMap { file -> protoMessageRegex.findAll(file.readText()).map { it.groupValues[1] } }
            .toSortedSet()

        val generatedCodecNames = generatedKotlinDir.walkTopDown()
            .filter { it.isFile && it.name.endsWith("Codec.kt") && !it.name.contains("SampleApp") }
            .map { it.nameWithoutExtension.removeSuffix("Codec") }
            .toSortedSet()

        // Codec names for nested classes use ParentName_ChildName format
        val missing = annotatedClasses.filter { className ->
            className !in generatedCodecNames &&
                generatedCodecNames.none { it.endsWith("_$className") }
        }.toSortedSet()

        assertTrue(
            "The following @ProtoMessage classes have no generated codec: $missing\n" +
                "Annotated classes (${annotatedClasses.size}): $annotatedClasses\n" +
                "Generated codecs (${generatedCodecNames.size}): $generatedCodecNames",
            missing.isEmpty()
        )
    }

    private fun File.onEachFile(action: (file: File) -> Unit) {
        listAllFiles().sorted().forEach { file -> action(file) }
    }

    private fun File.listAllFiles(): List<File> {
        val allFiles = mutableListOf<File>()
        listFiles().forEach { child ->
            if (child.isDirectory) {
                allFiles += child.listAllFiles()
            } else {
                allFiles += child
            }
        }
        return allFiles
    }
}
