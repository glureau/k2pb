package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.NullableStringConverter
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.annotation.ProtoName
import com.glureau.k2pb.annotation.ProtoNumber
import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.ProtobufAggregator
import com.glureau.k2pb.compiler.capitalizeUS
import com.glureau.k2pb.compiler.decapitalizeUS
import com.glureau.k2pb.compiler.getArg
import com.glureau.k2pb.compiler.sharedOptions
import com.glureau.k2pb.compiler.struct.FieldType
import com.glureau.k2pb.compiler.struct.ListType
import com.glureau.k2pb.compiler.struct.MapType
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.NullabilitySubField
import com.glureau.k2pb.compiler.struct.NumberManager
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.TypedField
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName


val KSClassDeclaration.isClass: Boolean
    get() = classKind == ClassKind.CLASS
val KSClassDeclaration.isDataClass: Boolean
    get() = classKind == ClassKind.CLASS && this.modifiers.contains(Modifier.DATA)
val KSClassDeclaration.isAbstractClass: Boolean
    get() = classKind == ClassKind.CLASS && this.modifiers.contains(Modifier.ABSTRACT)
val KSClassDeclaration.isSealed: Boolean
    get() = this.modifiers.contains(Modifier.SEALED)
val KSClassDeclaration.isInlineClass: Boolean
    get() = classKind == ClassKind.CLASS &&
            (this.modifiers.contains(Modifier.INLINE) || this.modifiers.contains(Modifier.VALUE))
val KSClassDeclaration.isObject: Boolean
    get() = classKind == ClassKind.OBJECT
val KSClassDeclaration.isEnum: Boolean
    get() = classKind == ClassKind.ENUM_CLASS

fun ProtobufAggregator.recordKSClassDeclaration(declaration: KSClassDeclaration) {
    when {
        declaration.isSealed || declaration.isAbstractClass -> recordNode(declaration.abstractToMessageNode())

        declaration.isDataClass -> recordNode(declaration.dataClassToMessageNode())
        declaration.isObject -> recordNode(declaration.mapObjectNode())
        declaration.isEnum -> recordNode(declaration.mapEnumNode())
        declaration.isInlineClass -> {
            recordNode(declaration.dataClassToMessageNode())
        }

        declaration.isClass -> {
            recordNode(declaration.dataClassToMessageNode())
        }

        else -> error("Unsupported class kind: ${declaration.simpleName.asString()} ${declaration.classKind} with modifiers: ${declaration.modifiers}")
    }
}

private fun KSClassDeclaration.abstractToMessageNode(): MessageNode {
    val subclasses = getSealedSubclasses().toList()
    val possibleValuesText = if (subclasses.isNotEmpty()) {
        "Possible values are:\n" +
                subclasses.joinToString("\n") { "- '${it.serialName}'" }
    } else {
        "(subclasses cannot be listed automatically)"
    }
    val sealedSubclassWithIndex: Map<ClassName, Int> =
        // TODO : Handle proto-numbers for sealed subclasses!
        subclasses.mapIndexed { i, c -> c.toClassName() to i + 1 }
            .toMap()

    return MessageNode(
        packageName = this.packageName.asString(),
        qualifiedName = this.qualifiedName!!.asString(),
        name = protobufName(),
        comment = if (sharedOptions.useKspPolymorphism) "${docString?.let { "$it\n" } ?: ""}Polymorphism structure for '${serialName}'\n$possibleValuesText" else "",
        isPolymorphic = true,
        isSealed = modifiers.contains(Modifier.SEALED),
        sealedSubClasses = subclasses.map { it.toClassName() },
        explicitGenerationRequested = false,
        superTypes = emptyList(),
        fields = if (sharedOptions.useKspPolymorphism)
            classNamesToOneOfField(
                fieldName = protobufName(),
                subclassesWithProtoNumber = sealedSubclassWithIndex
            )
        else emptyList(),
        isInlineClass = false,
        originalFile = containingFile,
    )
}

private fun KSClassDeclaration.dataClassToMessageNode(): MessageNode {
    val numberManager = NumberManager(1)
    val primaryCtor = primaryConstructor
    if (primaryCtor == null) {
        Logger.error("${this.simpleName.asString()} should have a primary constructor", this)
        Thread.sleep(3000)
        error("Primary constructor is required")
    }
    val fields = primaryCtor.parameters.mapNotNull { param ->
        val prop = this.getDeclaredProperties()
            .firstOrNull { it.simpleName == param.name } ?: return@mapNotNull null

        if (prop.annotations.any { it.shortName.asString() == "Transient" }) {
            Logger.info("Ignored transient field ${prop.serialName} on ${(qualifiedName ?: simpleName).asString()}")
            return@mapNotNull null
        }
        val resolvedType = prop.type.resolve()
        val resolvedDeclaration = resolvedType.declaration
        val isInlineClass = resolvedDeclaration is KSClassDeclaration && resolvedDeclaration.isInlineClass

        // For some reason, KSP doesn't always see the backing field in other modules... (1.9.25-1.0.20)
        /*
        if (!prop.hasBackingField) {
            Logger.info("Ignored property without backing field ${prop.serialName} on ${(qualifiedName ?: simpleName).asString()}")
            return@mapNotNull error("no backing field? $isInlineClass ${prop.simpleName.asString()} // ALL PROP = ${getAllProperties().joinToString { " ** " + it.simpleName.asString() + " / " + it.hasBackingField + " / " + it.type }}")
        }
        */


        if (isInlineClass) {
            val inlineProperty = resolvedDeclaration.getDeclaredProperties().first()
            val type = inlineProperty.type
            if (type is KSClassDeclaration) {
                TODO("HERE?")
            }
        }

        val annotatedConverter = prop.annotations.customConverter()
        val annotatedDerivedType = when (annotatedConverter) {
            is NullableStringConverter<*> -> ScalarFieldType.StringNullable
            else -> null
        }
        val annotatedNumber = prop.protoNumberInternal
        val propName = prop.simpleName.asString()
        when {
            resolvedDeclaration.modifiers.contains(Modifier.SEALED) -> {
                TypedField(
                    name = propName,
                    annotatedName = prop.serialName.decapitalizeUS(),
                    type = annotatedDerivedType ?: prop.type.toProtobufFieldType(),
                    comment = prop.docString,
                    annotatedConverter = annotatedConverter,
                    protoNumber = numberManager.resolve(propName, annotatedNumber),
                    nullabilitySubField = null,
                )
            }

            resolvedType.isError -> {
                Logger.error("Unknown type on ${(qualifiedName ?: simpleName).asString()}: ${prop.type} / ${prop.type.resolve()}")
                Logger.warn("You can use ksp arguments to replace a type with a custom serializer by another type")// TODO doc
                TypedField(
                    name = propName,
                    annotatedName = prop.serialName,
                    type = annotatedDerivedType ?: ReferenceType(
                        className = prop.type.resolve().toClassName(),
                        name = prop.type.toString(),
                        isNullable = prop.type.resolve().isMarkedNullable,
                        isEnum = prop.type.resolve().declaration.modifiers.contains(Modifier.ENUM),
                    ),
                    comment = prop.docString,
                    //annotatedNumber = annotatedNumber,
                    annotatedConverter = annotatedConverter,
                    protoNumber = numberManager.resolve(propName, annotatedNumber),
                    nullabilitySubField = null, // TODO: handle nullability
                )
            }

            else -> {
                val type = annotatedDerivedType ?: prop.type.toProtobufFieldType() // TODO: Common to 3 branches?
                TypedField(
                    name = propName,
                    annotatedName = prop.serialName,
                    type = type,
                    comment = prop.docString,
                    //annotatedNumber = annotatedNumber,
                    annotatedConverter = annotatedConverter,
                    protoNumber = numberManager.resolve(propName, annotatedNumber),
                    nullabilitySubField = null,
                ).withNullabilitySubFieldIfNeeded(numberManager)
            }
        }
    }
    if (this.isInlineClass) {
        require(fields.size == 1) {
            "One and only one field is allowed in an inline class: " +
                    "${this.qualifiedName!!.asString()}.\n" +
                    "Fields: $fields"
        }
    }
    return MessageNode(
        packageName = this.packageName.asString(),
        qualifiedName = this.qualifiedName!!.asString(),
        name = protobufName(),
        isPolymorphic = false,
        isSealed = false,
        explicitGenerationRequested = false,
        isInlineClass = this.isInlineClass,
        superTypes = this.getAllSuperTypes().map { it.toClassName() }
            .filterNot { it.canonicalName == "kotlin.Any" }
            .toList(),
        comment = docString, // because it's a data class
        fields = fields.toList(),
        originalFile = containingFile,
        sealedSubClasses = emptyList(),
    )
}

private fun TypedField.useNullabilitySubField(): Boolean =
    // If there's an inlined field is nullable or not is not relevant here, one nullability
    (type.isNullable && (type !is ReferenceType || type.inlineOf != null)) ||
            (type.isNullable && annotatedConverter.customConverterType()?.isNullable == true) ||
            (type is ReferenceType && type.inlineOf != null && type.inlineOf.isNullable) ||
            (type is ReferenceType && type.inlineAnnotatedSerializer is NullableStringConverter<*>)

private fun TypedField.withNullabilitySubFieldIfNeeded(numberManager: NumberManager): TypedField {
    return if (useNullabilitySubField()) {
        val nullFieldName = "is" + name.capitalizeUS() + "Null"
        copy(
            nullabilitySubField = NullabilitySubField(
                fieldName = nullFieldName,
                /* TODO : handle a custom number in annotation for this nullability field */
                protoNumber = numberManager.resolve(nullFieldName, null),
            )
        )
    } else {
        this
    }
}

private fun KSTypeReference.toProtobufFieldType(): FieldType {
    val declaration = this.resolve().declaration
    val qualifiedName = declaration.qualifiedName?.asString()
    return mapQfnToFieldType(
        qualifiedName ?: error(
            "Cannot resolve $this, please add the explicit dependency for it " +
                    "while k2pb dev try another implementation approach..."
        ), this.resolve()
    )
}

private fun mapQfnToFieldType(
    qfn: String,
    type: KSType? = null,
): FieldType {
    when (qfn) {
        "kotlin.String" -> ScalarFieldType.String
        "kotlin.Int" -> ScalarFieldType.Int
        "kotlin.Char" -> ScalarFieldType.Char
        "kotlin.Short" -> ScalarFieldType.Short
        "kotlin.Byte" -> ScalarFieldType.Byte
        "kotlin.Long" -> ScalarFieldType.Long
        "kotlin.Float" -> ScalarFieldType.Float
        "kotlin.Double" -> ScalarFieldType.Double
        "kotlin.Boolean" -> ScalarFieldType.Boolean
        "kotlin.ByteArray" -> ScalarFieldType.ByteArray
        else -> null
    }?.let {
        if (type?.isMarkedNullable == true) {
            return it.copy(isNullable = true)
        } else {
            return it
        }
    }

    return when (qfn) {
        "kotlin.collections.List" -> {
            ListType(
                repeatedType = type!!.arguments[0].type!!.toProtobufFieldType(), // TODO: List<List<Int>> is not supported
                isNullable = type.isMarkedNullable
            )
        }

        "kotlin.collections.Map" -> {
            MapType(
                keyType = type!!.arguments[0].type!!.toProtobufFieldType(), // TODO: Map<Map<X, X>, X> is not supported
                valueType = type.arguments[1].type!!.toProtobufFieldType(),
                isNullable = type.isMarkedNullable
            )
        }

        else -> {
            val typeDecl = type?.declaration as? KSClassDeclaration
            if (typeDecl?.isInlineClass == true) {
                val inlined = (type.declaration as KSClassDeclaration).getDeclaredProperties().first()
                val inlinedFieldType = inlined.type.toProtobufFieldType()
                val inlineAnnotatedSerializer = inlined.annotations.customConverter()
                ReferenceType(
                    className = type.toClassName(),
                    name = qfn,
                    isNullable = type.isMarkedNullable == true,// inlinedFieldType.isNullable,
                    inlineOf = inlinedFieldType,
                    inlineName = inlined.simpleName.asString(),
                    inlineAnnotatedSerializer = inlineAnnotatedSerializer,
                    isEnum = inlined.modifiers.contains(Modifier.ENUM),
                )
            } else {
                val isEnum = typeDecl?.isEnum == true
                // val converter = type?.annotations?.customConverter()
                ReferenceType(
                    className = typeDecl?.toClassName() ?: error("No type for $qfn"),
                    name = qfn,
                    isNullable = type.isMarkedNullable == true,
                    isEnum = isEnum,
                    enumFirstEntry = if (isEnum) {
                        typeDecl.declarations
                            .filterIsInstance<KSClassDeclaration>()
                            .firstOrNull()
                            ?.toClassName()
                    } else {
                        null
                    }
                )
            }
        }
    }
}


fun KSClassDeclaration.protobufName(): String {
    val p = parent as? KSClassDeclaration
    return ((if (p != null) p.protobufName() + "." else null) ?: "") +
            simpleName.asString()
}

val KSClassDeclaration.serialName: String
    get() = serialNameInternal ?: simpleName.asString()

val KSPropertyDeclaration.serialName: String
    get() = serialNameInternal ?: simpleName.asString()

private val KSAnnotated.serialNameInternal: String?
    get() =
        annotations.toList()
            .firstOrNull { it.shortName.asString() == ProtoName::class.simpleName }
            ?.getArg<String>(ProtoName::name)
            ?: annotations.toList()
                .firstOrNull { it.shortName.asString() == ProtoMessage::class.simpleName }
                ?.getArg<String>(ProtoMessage::name)
                ?.takeIf { it.isNotBlank() }


val KSClassDeclaration.protoNumber: Int?
    get() = protoNumberInternal

val KSPropertyDeclaration.protoNumber: Int?
    get() = protoNumberInternal

private val KSAnnotated.protoNumberInternal: Int?
    get() =
        annotations.toList()
            .firstOrNull { it.shortName.asString() == ProtoNumber::class.simpleName }
            ?.getArg<Int>(ProtoNumber::number)