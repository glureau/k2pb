package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.*
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.*


val KSClassDeclaration.isDataClass: Boolean
    get() = classKind == ClassKind.CLASS && this.modifiers.contains(Modifier.DATA)
val KSClassDeclaration.isAbstractClass: Boolean
    get() = classKind == ClassKind.CLASS && this.modifiers.contains(Modifier.ABSTRACT)
val KSClassDeclaration.isSealedClass: Boolean
    get() = classKind == ClassKind.CLASS && this.modifiers.contains(Modifier.SEALED)
val KSClassDeclaration.isSealedInterface: Boolean
    get() = classKind == ClassKind.INTERFACE && this.modifiers.contains(Modifier.SEALED)
val KSClassDeclaration.isInlineClass: Boolean
    get() = classKind == ClassKind.CLASS && this.modifiers.contains(Modifier.VALUE)
val KSClassDeclaration.isEnum: Boolean
    get() = classKind == ClassKind.ENUM_CLASS

fun ProtobufAggregator.recordKSClassDeclaration(declaration: KSClassDeclaration) {
    when {
        declaration.isSealedClass -> recordMessageNode(declaration.sealedClassToMessageNode())
        declaration.isDataClass -> recordMessageNode(declaration.dataClassToMessageNode())
        declaration.isEnum -> recordEnumNode(declaration.toProtobufEnumNode())
        declaration.isInlineClass -> {
            val inlinedFieldType = declaration.getDeclaredProperties().first().type.resolve().toProtobufFieldType()
            InlinedTypeRecorder.recordInlinedType(declaration.qualifiedName!!.asString(), inlinedFieldType)
        }

        declaration.isAbstractClass -> {
            // Cannot instantiate this class, so ignore it
        }
        // TODO: not sure about "class" and other kinds, crash for reporting them...
        else -> error("Unsupported class kind: ${declaration.classKind} with modifiers: ${declaration.modifiers}")
    }
}

private fun KSClassDeclaration.toProtobufEnumNode(): EnumNode {
    val entries = declarations.toList()
        .filterIsInstance<KSClassDeclaration>()
        .filter { it.classKind == ClassKind.ENUM_ENTRY }
        .mapIndexed { index, entry ->
            EnumEntry(
                name = entry.simpleName.asString(),
                comment = entry.docString,
                // TODO: handle annotations for name and number
                number = index, // proto3: enum starts at 0
            )
        }
    return EnumNode(
        qualifiedName = qualifiedName!!.asString(),
        name = protobufName(),
        comment = docString,
        entries = entries,
        originalFile = containingFile,
    )
}

private fun KSClassDeclaration.sealedClassToMessageNode(): MessageNode {
    OneOfRecorder.recordSealedClass(
        qualifiedName!!.asString(),
        getSealedSubclasses().map { it.qualifiedName!!.asString() }.toList()
    )
    // This is required to handle nested sealed classes
    // TODO: could be improved if we create it ONLY when there's nesting
    return MessageNode(
        qualifiedName = this.qualifiedName!!.asString(),
        name = protobufName(),
        comment = docString,
        fields = emptyList(),
        originalFile = containingFile
    )
}

private fun KSClassDeclaration.dataClassToMessageNode(): MessageNode {
    val fields = primaryConstructor!!.parameters.mapIndexed { index, param ->
        val prop = this.getDeclaredProperties().first { it.simpleName == param.name }
        val prop2 = this.getAllProperties().first { it.simpleName == param.name }

        val resolvedType = param.type.resolve()
        if (param.toString() == "dataClassFromLib") {
            Logger.warn("---------------------------")
            Logger.warn("param = $param", param)
            Logger.warn("param.type = ${param.type}", param.type)
            Logger.warn("param.type class = ${param.type::class}")
            Logger.warn("param.type.element = ${param.type.element}")
            Logger.warn("param.type.element class = ${param.type.element!!::class}")
            Logger.warn("param.type.element = ${(param.type.element as? KSClassifierReference)?.qualifier}")
            Logger.warn("param.type.element = ${(param.type.element as? KSClassifierReference)?.referencedName()}")
            Logger.warn("param.type.parent = ${param.type.parent}")
            Logger.warn("param.type.resolve() = $resolvedType")
            Logger.warn("prop = $prop", prop)
            Logger.warn("prop = ${prop.type}")
            Logger.warn("prop.type.element = ${prop.type.element}")
            Logger.warn("prop = ${prop.type.resolve()}")
            Logger.warn("prop2 = $prop2")
            Logger.warn("prop2 = ${prop2}")
            Logger.warn("prop2 = ${prop2.type.resolve()}")
            if (resolvedType.isError) {
                Logger.error("STOP HERE")
            }
        }
        if (resolvedType.declaration.modifiers.contains(Modifier.SEALED)) {
            OneOfField(
                name = param.name!!.asString(), // TODO annotation SerialName
                comment = prop.docString,
                fields = (resolvedType.declaration as KSClassDeclaration).getSealedSubclasses()
                    .mapIndexed { index, subclass ->
                        // TODO: Handle oneOf recursively
                        TypedField(
                            name = subclass.simpleName.asString(), // TODO annotation SerialName
                            type = subclass.asType(emptyList()).toProtobufFieldType(),
                            comment = subclass.docString,
                            number = index + 1, // TODO annotation + local increment
                        )
                    }.toList(),
            )
        } else if (resolvedType.isError) {

            TypedField(
                name = param.name!!.asString(), // TODO annotation SerialName
                type = ReferenceType(prop.type.toString()),
                comment = prop.docString,
                number = index + 1, // TODO annotation + local increment
            )
                .also { Logger.warn("resolvedType.isError -> $it") }
        } else {
            TypedField(
                name = param.name!!.asString(), // TODO annotation SerialName
                type = resolvedType.toProtobufFieldType(),
                comment = prop.docString,
                number = index + 1, // TODO annotation + local increment
            )
        }
    }
    return MessageNode(
        qualifiedName = this.qualifiedName!!.asString(),
        name = protobufName(),
        comment = docString,
        fields = fields,
        originalFile = containingFile
    )
}

private fun KSType.toProtobufFieldType(): FieldType {
    if (this.declaration.qualifiedName == null) {
        Logger.warn("Cannot resolve type ${this.declaration.simpleName.asString()} from ${this.declaration.containingFile} ($this)")
        Logger.exception(IllegalStateException("resolution issue"))
        return ReferenceType(this.declaration.simpleName.asString())
    }
    return when (val name = this.declaration.qualifiedName!!.asString()) {
        "kotlin.String" -> ScalarType.string
        "kotlin.Int" -> ScalarType.int32
        "kotlin.Char" -> ScalarType.int32
        "kotlin.Short" -> ScalarType.int32
        "kotlin.Byte" -> ScalarType.int32
        "kotlin.Long" -> ScalarType.int64
        "kotlin.Float" -> ScalarType.float
        "kotlin.Double" -> ScalarType.double
        "kotlin.Boolean" -> ScalarType.bool
        "kotlin.collections.List" -> ListType(
            repeatedType = arguments[0].type!!.resolve().toProtobufFieldType() // TODO: List<List<Int>> is not supported
        )

        "kotlin.collections.Map" -> MapType(
            keyType = arguments[0].type!!.resolve().toProtobufFieldType(), // TODO: Map<Map<X, X>, X> is not supported
            valueType = arguments[1].type!!.resolve().toProtobufFieldType(),
        )

        "kotlinx.datetime.Instant" -> ScalarType.string

        else -> {
            if (name.contains("error", false)) {
                Logger.warn("!!! Cannot resolve type $this from ${this.declaration.containingFile}")
            }
            ReferenceType(name)
        }
    }
}


fun KSClassDeclaration.protobufName(): String {
    val p = parent as? KSClassDeclaration
    return ((if (p != null) p.protobufName() + "." else null) ?: "") +
            simpleName.asString()
}
