package com.glureau.k2pb.compiler.protofile

fun StringBuilder.appendComment(indentLevel: Int, comment: String?) {
    if (!comment.isNullOrBlank()) {
        comment.split("\n")
            .dropWhile { it.isBlank() }
            .dropLastWhile { it.isBlank() }
            .forEach {
                appendLineWithIndent(indentLevel, "// $it")
            }
    }
}
