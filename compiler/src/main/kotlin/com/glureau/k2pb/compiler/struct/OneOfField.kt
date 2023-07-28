package com.glureau.k2pb.compiler.struct

data class OneOfField(
    override val comment: String?,
    override val name: String,
    val fields: List<FieldInterface>,
) : FieldInterface {
    override fun toString(numberManager: NumberManager): String {
        var result = ""
        result += "oneof $name {\n"
        result += fields.joinToString("\n") { it.toString(numberManager).prependIndent("  ") }
        result += "\n}"
        return result
    }
}
