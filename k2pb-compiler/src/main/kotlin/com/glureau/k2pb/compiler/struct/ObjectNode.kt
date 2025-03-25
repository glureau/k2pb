package com.glureau.k2pb.compiler.struct

import com.google.devtools.ksp.symbol.KSFile

data class ObjectNode(
    override val packageName: String,
    override val qualifiedName: String,
    override val name: String,
    override val protoName: String,
    val comment: String?,
    override val originalFile: KSFile?,
) : Node() {
    override val generatesNow: Boolean get() = true
}
