package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.struct.EnumEntry
import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.NumberManager
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun KSClassDeclaration.mapEnumNode(): EnumNode {
    val numberManager = NumberManager(0)
    val entries = declarations.toList()
        .filterIsInstance<KSClassDeclaration>()
        .filter { it.classKind == ClassKind.ENUM_ENTRY }
        .map { entry ->
            val name = entry.annotatedProtoNameOrSimpleName
            EnumEntry(
                name = name,
                comment = entry.docString,
                number = numberManager.resolve(name, entry.protoNumber), // proto3: enum starts at 0
            )
        }
    return EnumNode(
        packageName = this.packageName.asString(),
        qualifiedName = qualifiedName!!.asString(),
        name = protobufName(),
        protoName = annotatedProtoNameOrNull ?: protobufName(),
        comment = docString,
        entries = entries,
        originalFile = containingFile,
    )
}