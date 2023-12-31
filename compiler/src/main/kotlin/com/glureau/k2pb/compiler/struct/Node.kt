package com.glureau.k2pb.compiler.struct

import com.google.devtools.ksp.symbol.KSFile

sealed class Node {
    abstract val qualifiedName: String
    abstract val name: String
    abstract val originalFile: KSFile?
}
