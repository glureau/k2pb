package sample

import org.approvaltests.Approvals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class GeneratedClassesTest {

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
