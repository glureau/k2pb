package com.glureau.k2pb.compiler.struct

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName

data class MessageNode(
    override val packageName: String,
    override val qualifiedName: String,
    override val name: String,
    override val protoName: String,
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
    val deprecatedFields: List<IDeprecatedField>,
    override val originalFile: KSFile?,
    val sealedSubClasses: List<ClassName>,
) : Node() {

    // If the generation is not explicitly requested, polymorphic unsealed classes are skipped,
    // as they are generated in the final module (via an explicit annotation).
    override val generatesNow: Boolean
        get() = explicitGenerationRequested ||
                !isPolymorphic ||
                isSealed
}

fun Node.asClassName(): ClassName = ClassName(packageName, name.split("."))
fun Node.codecClassName(): ClassName = ClassName(packageName, "${name.replace(".", "_")}Codec")
