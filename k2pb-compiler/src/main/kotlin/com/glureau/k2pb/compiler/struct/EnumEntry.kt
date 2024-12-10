package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.appendComment

data class EnumEntry(val name: String, val comment: String?, val number: Int)

fun StringBuilder.appendEnumEntry(indentLevel: Int, enumEntry: EnumEntry) {
    appendComment(indentLevel, enumEntry.comment)
    appendLineWithIndent(indentLevel, "${enumEntry.name} = ${enumEntry.number};")
}