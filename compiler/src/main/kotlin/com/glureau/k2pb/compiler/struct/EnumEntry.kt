package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.mapping.toProtobufComment

data class EnumEntry(val name: String, val comment: String?, val number: Int) {
    override fun toString(): String {
        var result = ""
        result += comment.toProtobufComment()
        return "$result$name = $number;"
    }
}
