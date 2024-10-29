package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.CustomStringSerializer
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.annotation.ProtoName
import com.glureau.k2pb.annotation.ProtoNumber
import com.glureau.k2pb.annotation.ProtoStringSerializer
import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.ProtobufAggregator
import com.glureau.k2pb.compiler.getArg
import com.glureau.k2pb.compiler.sharedOptions
import com.glureau.k2pb.compiler.struct.EnumEntry
import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.FieldType
import com.glureau.k2pb.compiler.struct.ListType
import com.glureau.k2pb.compiler.struct.MapType
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.NumberManager
import com.glureau.k2pb.compiler.struct.OneOfField
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.appendLineWithIndent
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ksp.toClassName
import java.util.Locale


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
        declaration.isSealed || declaration.isAbstractClass -> recordMessageNode(declaration.abstractToMessageNode())
        declaration.isDataClass -> recordMessageNode(declaration.dataClassToMessageNode())
        declaration.isObject -> recordMessageNode(declaration.objectToMessageNode())
        declaration.isEnum -> recordEnumNode(declaration.toProtobufEnumNode())
        declaration.isInlineClass -> {
            val inlinedFieldType = declaration.getDeclaredProperties().first().type.toProtobufFieldType()
            InlinedTypeRecorder.recordInlinedType(declaration.qualifiedName!!.asString(), inlinedFieldType)
            //recordMessageNode(declaration.dataClassToMessageNode())
        }

        declaration.isClass -> recordMessageNode(declaration.dataClassToMessageNode())

        else -> error("Unsupported class kind: ${declaration.simpleName.asString()} ${declaration.classKind} with modifiers: ${declaration.modifiers}")
    }
}

private fun KSClassDeclaration.toProtobufEnumNode(): EnumNode {
    val numberManager = NumberManager(0)
    val entries = declarations.toList()
        .filterIsInstance<KSClassDeclaration>()
        .filter { it.classKind == ClassKind.ENUM_ENTRY }
        .map { entry ->
            val name = entry.serialName
            EnumEntry(
                name = name,
                comment = entry.docString,
                number = numberManager.resolve(name, entry.protoNumber), // proto3: enum starts at 0
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

private fun KSClassDeclaration.abstractToMessageNode(): MessageNode {
    Logger.warn("Recording of abstract class is not supported yet: ${qualifiedName!!.asString()}")
    val subclasses = getSealedSubclasses().toList()
    val possibleValuesText = if (subclasses.isNotEmpty()) {
        "Possible values are:\n" +
                subclasses.joinToString("\n") { "- '${it.serialName}'" }
    } else {
        "(subclasses cannot be listed automatically)"
    }

    return MessageNode(
        packageName = this.packageName.asString(),
        qualifiedName = this.qualifiedName!!.asString(),
        name = protobufName(),
        comment = if (sharedOptions.useKspPolymorphism) "${docString?.let { "$it\n" } ?: ""}Polymorphism structure for '${serialName}'" else "",
        isObject = false,
        isPolymorphic = true,
        superTypes = emptyList(),
        fields = if (sharedOptions.useKspPolymorphism)
            listOf(
                OneOfField(
                    comment = "NOT SUPPORTED YET!",
                    name = protobufName().replaceFirstChar { it.lowercase(Locale.US) },
                    protoNumber = 1,
                    fields = emptyList()
                )
            ) else emptyList(),
        isInlineClass = false,
        originalFile = containingFile,
    )
}

private fun KSClassDeclaration.dataClassToMessageNode(): MessageNode {
    var currentProtoNumber = 1
    val fields = primaryConstructor!!.parameters.mapNotNull { param ->
        val prop = this.getDeclaredProperties().first { it.simpleName == param.name }

        //val fields = getDeclaredProperties().mapNotNull { prop ->
        if (prop.annotations.any { it.shortName.asString() == "Transient" }) {
            Logger.info("Ignored transient field ${prop.serialName} on ${(qualifiedName ?: simpleName).asString()}")
            return@mapNotNull null
        }
        if (!prop.hasBackingField) {
            Logger.info("Ignored property without backing field ${prop.serialName} on ${(qualifiedName ?: simpleName).asString()}")
            return@mapNotNull null
        }
        val resolvedType = prop.type.resolve()
        val resolvedDeclaration = resolvedType.declaration
        if (resolvedDeclaration is KSClassDeclaration &&
            (resolvedDeclaration.modifiers.contains(Modifier.INLINE) || resolvedDeclaration.modifiers.contains(Modifier.VALUE))
        ) {
            val type = resolvedDeclaration.getDeclaredProperties().first().type
            val inlinedFieldType = type.toProtobufFieldType()
            InlinedTypeRecorder.recordInlinedType(resolvedDeclaration.qualifiedName!!.asString(), inlinedFieldType)
        }

        val replacement = sharedOptions.replace(prop.type.toString())
        // TODO: Handle all serializers... eventually remove the replacement via options
        val annotatedSerializer = prop.annotations
            .firstOrNull { it.shortName.asString() == ProtoStringSerializer::class.simpleName }
            ?.getArg<KSType?>(ProtoStringSerializer::serializer)
        val annotatedDerivedType = when (annotatedSerializer) {
            is CustomStringSerializer<*> -> ScalarFieldType.String
            else -> null
        }
        val annotatedNumber = prop.protoNumberInternal
        if (annotatedNumber != null && currentProtoNumber < annotatedNumber) {
            Logger.warn(
                "Proto number is not sequential on ${(qualifiedName ?: simpleName).asString()}: " +
                        "${prop.serialName} ($currentProtoNumber -> $annotatedNumber)"
            )
            currentProtoNumber = annotatedNumber
        }
        when {
            replacement != null -> {
                TypedField(
                    name = prop.simpleName.asString(),
                    annotatedName = prop.serialName,
                    type = annotatedDerivedType ?: mapQfnToFieldType(replacement)
                        .also { Logger.warn("GREG - A - ${prop.modifiers}") },
                    comment = prop.docString,
                    annotatedNumber = annotatedNumber,
                    annotatedSerializer = annotatedSerializer,
                    protoNumber = currentProtoNumber++,
                )
            }

            resolvedDeclaration.modifiers.contains(Modifier.SEALED) -> {
                TypedField(
                    name = prop.simpleName.asString(),
                    annotatedName = prop.serialName.replaceFirstChar { it.lowercase(Locale.getDefault()) },
                    type = annotatedDerivedType ?: prop.type.toProtobufFieldType(),
                    comment = prop.docString,
                    annotatedNumber = annotatedNumber,
                    annotatedSerializer = annotatedSerializer,
                    protoNumber = currentProtoNumber++,
                )
            }

            resolvedType.isError -> {
                Logger.warn("Unknown type on ${(qualifiedName ?: simpleName).asString()}: ${prop.type} / ${prop.type.resolve()}")
                Logger.warn("You can use ksp arguments to replace a type with a custom serializer by another type")
                Logger.warn("options.replacementMap = ${sharedOptions.replacementMap}")
                TypedField(
                    name = prop.simpleName.asString(),
                    annotatedName = prop.serialName,
                    type = annotatedDerivedType ?: ReferenceType(
                        prop.type.toString(),
                        prop.type.resolve().isMarkedNullable,
                    ).also {
                        Logger.warn("GREG - ${prop.simpleName} - ${prop.type.resolve().declaration.modifiers}")
                    },
                    comment = prop.docString,
                    annotatedNumber = annotatedNumber,
                    annotatedSerializer = annotatedSerializer,
                    protoNumber = currentProtoNumber++,
                )
                    .also { Logger.warn("resolvedType.isError -> $it") }
            }

            else -> {
                TypedField(
                    name = prop.simpleName.asString(),
                    annotatedName = prop.serialName,
                    type = annotatedDerivedType ?: prop.type.toProtobufFieldType(),
                    comment = prop.docString,
                    annotatedNumber = annotatedNumber,
                    annotatedSerializer = annotatedSerializer,
                    protoNumber = currentProtoNumber++,
                )
            }
        }
    }
    return MessageNode(
        packageName = this.packageName.asString(),
        qualifiedName = this.qualifiedName!!.asString(),
        name = protobufName(),
        comment = docString,
        fields = fields.toList(),
        originalFile = containingFile,
        isPolymorphic = false,
        isObject = false, // because it's a data class
        isInlineClass = this.isInlineClass,
        superTypes = this.superTypes.map { it.resolve().toClassName() }
            .filterNot { it.canonicalName == "kotlin.Any" }
            .toList(),
    )
}

private fun KSClassDeclaration.objectToMessageNode(): MessageNode = MessageNode(
    packageName = this.packageName.asString(),
    qualifiedName = qualifiedName!!.asString(),
    name = protobufName(),
    comment = docString,
    // `object` can be serialized, also as the data is static, fields are not serialized
    fields = emptyList(),
    originalFile = containingFile,
    isPolymorphic = false,
    isObject = true,
    isInlineClass = false,
    superTypes = emptyList(),
)

private fun KSTypeReference.toProtobufFieldType(): FieldType {
    val declaration = this.resolve().declaration
    val qualifiedName = declaration.qualifiedName?.asString()
    val resolvedQualifiedName = sharedOptions.replace(this.toString()) ?: qualifiedName
    if (resolvedQualifiedName == null) {
        // TODO: TU for that case
        Logger.warn("Cannot resolve declaration for $qualifiedName from ${declaration.containingFile} ($this)")
        Logger.exception(IllegalStateException("resolution issue: $declaration"))
        return ReferenceType(declaration.simpleName.asString(), this.resolve().isMarkedNullable)
            .also { Logger.warn("GREG - ${declaration.simpleName} - ${declaration.modifiers}") }
    }

    return mapQfnToFieldType(resolvedQualifiedName, this.resolve())
        .also { Logger.warn("GREG - B - ${this.resolve().declaration.modifiers}") }
}

private fun mapQfnToFieldType(
    qfn: String,
    type: KSType? = null,
): FieldType {
    Logger.warn(
        "GREG - mapQfnToFieldType - $qfn - $type - ${type?.declaration?.modifiers} - ${
            (type?.declaration as? KSClassDeclaration)?.getDeclaredProperties()?.toList()
        }"
    )
    return when (qfn) {
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
        "kotlin.collections.List" -> {
            ListType(
                repeatedType = type!!.arguments[0].type!!.toProtobufFieldType() // TODO: List<List<Int>> is not supported
            )
        }

        "kotlin.collections.Map" -> {
            MapType(
                keyType = type!!.arguments[0].type!!.toProtobufFieldType(), // TODO: Map<Map<X, X>, X> is not supported
                valueType = type.arguments[1].type!!.toProtobufFieldType(),
            )
        }

        "kotlinx.datetime.Instant" -> ScalarFieldType.Instant

        // TODO: Consider nullability for the scalar types too!!
        else -> {
            if (type?.declaration?.modifiers?.contains(Modifier.VALUE) == true) {
                val inlined = (type.declaration as KSClassDeclaration).getDeclaredProperties().first()
                val inlinedFieldType = inlined.type.toProtobufFieldType()
                val inlineAnnotatedSerializer = inlined.annotations
                    .firstOrNull { it.shortName.asString() == ProtoStringSerializer::class.simpleName }
                    ?.getArg<KSType?>(ProtoStringSerializer::serializer)

                Logger.warn("GREG - ${type.declaration.simpleName.asString()} is inlined $inlinedFieldType / $inlineAnnotatedSerializer")

                ReferenceType(
                    name = qfn,
                    isNullable = false,
                    inlineOf = inlinedFieldType,
                    inlineName = inlined.simpleName.asString(),
                    inlineAnnotatedSerializer = inlineAnnotatedSerializer
                )
            } else {
                ReferenceType(qfn, false, null)
            }
        }
    }
}


fun KSClassDeclaration.protobufName(): String {
    val p = parent as? KSClassDeclaration
    return ((if (p != null) p.protobufName() + "." else null) ?: "") +
            simpleName.asString()
}

fun StringBuilder.appendComment(indentLevel: Int, comment: String?) {
    if (!comment.isNullOrBlank()) {
        comment.split("\n")
            .dropWhile { it.isBlank() }
            .dropLastWhile { it.isBlank() }
            .forEach {
                appendLineWithIndent(indentLevel, "// $it")
            }
    }
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