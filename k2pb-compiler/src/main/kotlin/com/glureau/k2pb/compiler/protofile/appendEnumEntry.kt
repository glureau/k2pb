package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.EnumEntry

fun StringBuilder.appendEnumEntry(indentLevel: Int, enumEntry: EnumEntry) {
    appendComment(indentLevel, enumEntry.comment)
    appendLineWithIndent(indentLevel, "${enumEntry.name} = ${enumEntry.number};")
}