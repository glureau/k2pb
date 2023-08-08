package com.glureau.k2pb.compiler

class OptionManager(options: Map<String, String>) {
    internal val replacementMap: Map<String, String> = options["k2pb:replacement"].orEmpty()
        .split(";") // each replacement is separated by a semicolon
        .groupBy({ it.substringBefore("=") }, { it.substringAfter("=") })
        .mapValues { it.value.first() }

    fun replace(asString: String): String? = replacementMap[asString]
}
