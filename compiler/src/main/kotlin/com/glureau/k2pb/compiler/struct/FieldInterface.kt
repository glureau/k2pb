package com.glureau.k2pb.compiler.struct

sealed interface FieldInterface {
    val comment: String?
    val name: String
    fun toString(numberManager: NumberManager): String
}
