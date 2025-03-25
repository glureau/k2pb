package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.struct.ObjectNode
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun KSClassDeclaration.mapObjectNode(): ObjectNode = ObjectNode(
    packageName = this.packageName.asString(),
    qualifiedName = qualifiedName!!.asString(),
    name = protobufName(),
    protoName = serialNameOrNull ?: protobufName(),
    comment = docString,
    originalFile = containingFile,
)
