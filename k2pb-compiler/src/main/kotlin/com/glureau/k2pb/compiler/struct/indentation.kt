package com.glureau.k2pb.compiler.struct

private val indentsCache = mutableMapOf<Int, String>()
fun indentation(level: Int): String = indentsCache.getOrPut(level) { "  ".repeat(level) }

fun StringBuilder.appendWithIndent(indentLevel: Int, text: String) {
    if (text.isNotBlank()) {
        append(indentation(indentLevel))
        append(text)
    }
}

fun StringBuilder.appendLineWithIndent(indentLevel: Int, text: String) {
    if (text.isNotBlank()) {
        append(indentation(indentLevel))
        appendLine(text)
    }
}