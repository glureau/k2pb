package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.mapping.annotatedProtoNameOrNull
import com.glureau.k2pb.compiler.struct.Node
import com.google.devtools.ksp.symbol.KSClassDeclaration

// TODO: static TypeResolver is not a good idea (especially for tests)
object TypeResolver {
    private val qualifiedNameToProtobufName = mutableMapOf<String, String>()

    fun record(reference: KSClassDeclaration) {
        qualifiedNameToProtobufName[reference.qualifiedName!!.asString()] =
                /*reference.serialNameOrNull?.let { sn ->
                    //compileOptions.protoPackageName?.let { packageName -> "$packageName.$sn" } ?: sn
                    sn
                } ?: */
            reference.simpleName.asString()
    }

    fun recordNode(node: Node) {
        qualifiedNameToProtobufName[node.qualifiedName] = node.protoName
    }

    fun resolveName(qfn: String) = qualifiedNameToProtobufName[qfn]
}