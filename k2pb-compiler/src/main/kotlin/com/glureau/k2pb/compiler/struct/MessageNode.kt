package com.glureau.k2pb.compiler.struct

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName

data class MessageNode(
    override val packageName: String,
    override val qualifiedName: String,
    override val name: String,
    val isPolymorphic: Boolean,
    // if not sealed, the generation is done in final module
    // if sealed, the generation is done in the current module
    // see [explicitGenerationRequested]
    val isSealed: Boolean,
    val explicitGenerationRequested: Boolean,
    val isInlineClass: Boolean,
    val superTypes: List<ClassName>,
    val comment: String?,
    val fields: List<FieldInterface>,
    override val originalFile: KSFile?,
    val sealedSubClasses: List<ClassName>,
    val customBuilder: ClassName?,
) : Node() {
    val numberManager = NumberManager()

    // If the generation is not explicitly requested, polymorphic unsealed classes are skipped,
    // as they are generated in the final module (via an explicit annotation).
    override val generatesNow: Boolean
        get() = explicitGenerationRequested ||
                !isPolymorphic ||
                isSealed
/*
    val dependencies: List<KSFile>
        get() {
            val result = mutableListOf<KSFile>()
            originalFile?.let { result.add(it) }
            nestedNodes.forEach { node ->
                node.originalFile?.let { result.add(it) }
            }
            return result
        }
*/
}

fun Node.asClassName(): ClassName = ClassName(packageName, name.split("."))
fun Node.serializerClassName(): ClassName = ClassName(packageName, "${name.replace(".", "_")}Serializer")
