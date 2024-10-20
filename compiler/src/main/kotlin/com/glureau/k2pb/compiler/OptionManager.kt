package com.glureau.k2pb.compiler

class OptionManager(options: Map<String, String>) {

    data class Replacement(val name: String, val shouldImport: Boolean)

    internal val replacementMap: Map<String, Replacement> = options["k2pb:replacement"].orEmpty()
        .split(";") // each replacement is separated by a semicolon
        .groupBy({ it.substringBefore("=") }, { it.substringAfter("=") })
        .mapValues {
            val data = it.value.first()
            val shouldImport = data.endsWith("[IMPORT]")
            Replacement(name = data.removeSuffix("[IMPORT]"), shouldImport = shouldImport)
        }

    internal var useKspPolymorphism = true // To be handled via options?

    fun replace(className: String): String? = replacementMap[className]?.name
    fun shouldImportForReplace(className: String): Boolean? = replacementMap[className]?.shouldImport
}
