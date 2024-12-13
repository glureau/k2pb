package com.glureau.k2pb.compiler.protofile

private val indentsCache = mutableMapOf<Int, String>()
fun indentation(level: Int): String = indentsCache.getOrPut(level) { "  ".repeat(level) }

fun StringBuilder.appendLineWithIndent(indentLevel: Int, text: String) {
    if (text.isNotBlank()) {
        append(indentation(indentLevel))
        appendLine(text)
    }
}