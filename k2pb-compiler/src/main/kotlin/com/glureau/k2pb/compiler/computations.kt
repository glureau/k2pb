package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.protofile.ProtobufFile
import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.Node
import com.glureau.k2pb.compiler.struct.ObjectNode
import com.google.devtools.ksp.symbol.KSFile


val ProtobufFile.dependencies: List<KSFile>
    get() = nodes.mapNotNull { it.originalFile }.distinct()

internal val Node.declaredReferences: List<String>
    get() = when (this) {
        is MessageNode -> this.nestedNodes.flatMap { it.declaredReferences } + this.qualifiedName
        is EnumNode -> listOf(this.qualifiedName)
        is ObjectNode -> emptyList()
    }

fun interface ImportResolver {
    // Return file to import
    fun resolve(protobufName: String): String
}
