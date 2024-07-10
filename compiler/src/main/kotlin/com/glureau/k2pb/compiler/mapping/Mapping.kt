package com.glureau.k2pb.compiler.mapping

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
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarType
import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.appendLineWithIndent
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.impl.hasAnnotation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.protobuf.ProtoNumber
import java.util.Locale


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
        }

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
    val subclasses = getSealedSubclasses().toList()
    val possibleValuesText = if (subclasses.isNotEmpty()) {
        "Possible values are:\n" +
                subclasses.joinToString("\n") { "- '${it.serialName}'" }
    } else {
        "(subclasses cannot be listed automatically)"
    }
    return MessageNode(
        qualifiedName = this.qualifiedName!!.asString(),
        name = protobufName(),
        comment = "${docString?.let { "$it\n" } ?: ""}Polymorphism structure for '${serialName}'",
        fields = listOf(
            TypedField(
                comment =
                "Serial name of the class implementing the interface/sealed class.\n" +
                        possibleValuesText,
                type = ScalarType.string,
                name = "type",
                annotatedNumber = 1
            ),
            TypedField(
                comment = "Data to be deserialized based on the field 'type'",
                type = ScalarType.bytes,
                name = "value",
                annotatedNumber = 2
            )
        ),
        originalFile = containingFile
    )
}

private fun KSClassDeclaration.dataClassToMessageNode(): MessageNode {
    val fields = primaryConstructor!!.parameters.mapNotNull { param ->
        val prop = this.getDeclaredProperties().first { it.simpleName == param.name }
        if (prop.hasAnnotation("kotlinx.serialization.Transient")) {
            Logger.warn("Ignored transient field ${prop.serialName} on ${(qualifiedName ?: simpleName).asString()}")
            return@mapNotNull null
        }
        val resolvedType = param.type.resolve()
        val resolvedDeclaration = resolvedType.declaration
        if (resolvedDeclaration is KSClassDeclaration &&
            (resolvedDeclaration.modifiers.contains(Modifier.INLINE) || resolvedDeclaration.modifiers.contains(Modifier.VALUE))
        ) {
            val type = resolvedDeclaration.getDeclaredProperties().first().type
            val inlinedFieldType = type.toProtobufFieldType()
            InlinedTypeRecorder.recordInlinedType(resolvedDeclaration.qualifiedName!!.asString(), inlinedFieldType)
        }

        val replacement = sharedOptions.replace(prop.type.toString())
        when {
            replacement != null -> {
                TypedField(
                    name = prop.serialName,
                    type = mapQfnToFieldType(replacement),
                    comment = prop.docString,
                    annotatedNumber = prop.protoNumber,
                )
            }

            resolvedDeclaration.modifiers.contains(Modifier.SEALED) -> {
                TypedField(
                    name = prop.serialName.replaceFirstChar { it.lowercase(Locale.getDefault()) },
                    type = prop.type.toProtobufFieldType(),
                    comment = prop.docString,
                    annotatedNumber = prop.protoNumber,
                )
            }

            resolvedType.isError -> {
                Logger.warn("Unknown type on ${(qualifiedName ?: simpleName).asString()}: ${prop.type} / ${prop.type.resolve()}")
                Logger.warn("You can use ksp arguments to replace a type with a custom serializer by another type")
                Logger.warn("options.replacementMap = ${sharedOptions.replacementMap}")
                TypedField(
                    name = prop.serialName,
                    type = ReferenceType(prop.type.toString()),
                    comment = prop.docString,
                    annotatedNumber = prop.protoNumber,
                )
                    .also { Logger.warn("resolvedType.isError -> $it") }
            }

            else -> {
                TypedField(
                    name = prop.serialName,
                    type = prop.type.toProtobufFieldType(),
                    comment = prop.docString,
                    annotatedNumber = prop.protoNumber,
                )
            }
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

private fun KSClassDeclaration.objectToMessageNode(): MessageNode = MessageNode(
    qualifiedName = qualifiedName!!.asString(),
    name = protobufName(),
    comment = docString,
    // `object` can be serialized, also as the data is static, fields are not serialized
    fields = emptyList(),
    originalFile = containingFile
)

private fun KSTypeReference.toProtobufFieldType(): FieldType {
    val declaration = this.resolve().declaration
    val qualifiedName = declaration.qualifiedName?.asString()
    val resolvedQualifiedName = sharedOptions.replace(this.toString()) ?: qualifiedName
    if (resolvedQualifiedName == null) {
        // TODO: TU for that case
        Logger.warn("Cannot resolve declaration for $qualifiedName from ${declaration.containingFile} ($this)")
        Logger.exception(IllegalStateException("resolution issue: $declaration"))
        return ReferenceType(declaration.simpleName.asString())
    }

    return mapQfnToFieldType(resolvedQualifiedName, this.resolve().arguments)
}

private fun mapQfnToFieldType(qfn: String, arguments: List<KSTypeArgument> = emptyList()): FieldType {
    return when (qfn) {
        "kotlin.String" -> ScalarType.string
        "kotlin.Int" -> ScalarType.int32
        "kotlin.Char" -> ScalarType.int32
        "kotlin.Short" -> ScalarType.int32
        "kotlin.Byte" -> ScalarType.int32
        "kotlin.Long" -> ScalarType.int64
        "kotlin.Float" -> ScalarType.float
        "kotlin.Double" -> ScalarType.double
        "kotlin.Boolean" -> ScalarType.bool
        "kotlin.ByteArray" -> ScalarType.bytes
        "kotlin.collections.List" -> {
            ListType(
                repeatedType = arguments[0].type!!.toProtobufFieldType() // TODO: List<List<Int>> is not supported
            )
        }

        "kotlin.collections.Map" -> {
            MapType(
                keyType = arguments[0].type!!.toProtobufFieldType(), // TODO: Map<Map<X, X>, X> is not supported
                valueType = arguments[1].type!!.toProtobufFieldType(),
            )
        }

        "kotlinx.datetime.Instant" -> ScalarType.string

        else -> ReferenceType(qfn)
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
            .firstOrNull { it.shortName.asString() == SerialName::class.simpleName }
            ?.getArg<String>(SerialName::value)

val KSClassDeclaration.protoNumber: Int?
    get() = protoNumberInternal

val KSPropertyDeclaration.protoNumber: Int?
    get() = protoNumberInternal

@OptIn(ExperimentalSerializationApi::class)
private val KSAnnotated.protoNumberInternal: Int?
    get() =
        annotations.toList()
            .firstOrNull { it.shortName.asString() == ProtoNumber::class.simpleName }
            ?.getArg<Int>(ProtoNumber::number)