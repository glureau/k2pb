package com.glureau.k2pb.compiler.mapping

import com.glureau.k2pb.compiler.Logger
import com.glureau.k2pb.compiler.ProtobufAggregator
import com.glureau.k2pb.compiler.getArg
import com.glureau.k2pb.compiler.struct.*
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.protobuf.ProtoNumber
import java.util.*


val KSClassDeclaration.isDataClass: Boolean
    get() = classKind == ClassKind.CLASS && this.modifiers.contains(Modifier.DATA)
val KSClassDeclaration.isAbstractClass: Boolean
    get() = classKind == ClassKind.CLASS && this.modifiers.contains(Modifier.ABSTRACT)
val KSClassDeclaration.isSealed: Boolean
    get() = this.modifiers.contains(Modifier.SEALED)
val KSClassDeclaration.isInlineClass: Boolean
    get() = classKind == ClassKind.CLASS && this.modifiers.contains(Modifier.VALUE)
val KSClassDeclaration.isEnum: Boolean
    get() = classKind == ClassKind.ENUM_CLASS

fun ProtobufAggregator.recordKSClassDeclaration(declaration: KSClassDeclaration) {
    when {
        declaration.isSealed -> recordMessageNode(declaration.sealedToMessageNode())
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

private fun KSClassDeclaration.sealedToMessageNode(): MessageNode {
    return MessageNode(
        qualifiedName = this.qualifiedName!!.asString(),
        name = protobufName(),
        comment = "${docString?.let { "$it\n" } ?: ""}Polymorphism structure for '${serialName}'",
        fields = listOf(
            TypedField(
                comment =
                "Serial name of the class implementing the interface/sealed class.\n" +
                        "Possible values are:\n" +
                        getSealedSubclasses().joinToString("\n") { "- '${it.serialName}'" },
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
    val fields = primaryConstructor!!.parameters.map { param ->
        val prop = this.getDeclaredProperties().first { it.simpleName == param.name }
        val prop2 = this.getAllProperties().first { it.simpleName == param.name }

        val resolvedType = param.type.resolve()
        if (param.toString() == "dataClassFromLib" || param.toString() == "valueClassFromLib") {
            Logger.warn("---------------------------")
            Logger.warn("param = $param", param)
            Logger.warn("param.type = ${param.type}", param.type)
            Logger.warn("param.type class = ${param.type::class}")
            Logger.warn("param.type.element = ${param.type.element}")
            Logger.warn("param.type.element class = ${param.type.element!!::class}")
            Logger.warn("param.type.element = ${(param.type.element as? KSClassifierReference)?.qualifier}")
            Logger.warn("param.type.element = ${(param.type.element as? KSClassifierReference)?.referencedName()}")
            Logger.warn("param.type.parent = ${param.type.parent}")
            Logger.error("param.type.resolve() = $resolvedType")
            Logger.warn("prop = $prop", prop)
            Logger.warn("prop = ${prop.type}")
            Logger.warn("prop.type.element = ${prop.type.element}")
            Logger.error("prop = ${prop.type.resolve()}")
            Logger.warn("prop2 = $prop2")
            Logger.error("prop2 = ${prop2.type.resolve()}")
            if (resolvedType.isError) {
                Logger.error("STOP HERE")
            }
        }
        if (resolvedType.declaration.modifiers.contains(Modifier.SEALED)) {
            TypedField(
                name = prop.serialName.replaceFirstChar { it.lowercase(Locale.getDefault()) },
                type = resolvedType.toProtobufFieldType(),
                comment = prop.docString,
                annotatedNumber = prop.protoNumber,
            )
        } else if (resolvedType.isError) {
            Logger.warn("WRONG NAME PATH: ${prop.type.toString()}")
            TypedField(
                name = prop.serialName,
                type = ReferenceType(prop.type.toString()),
                comment = prop.docString,
                annotatedNumber = prop.protoNumber,
            )
                .also { Logger.warn("resolvedType.isError -> $it") }
        } else {
            TypedField(
                name = prop.serialName,
                type = resolvedType.toProtobufFieldType(),
                comment = prop.docString,
                annotatedNumber = prop.protoNumber,
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
            Logger.warn("SEEING REF: $name")
            ReferenceType(name)
        }
    }
}


fun KSClassDeclaration.protobufName(): String {
    val p = parent as? KSClassDeclaration
    return ((if (p != null) p.protobufName() + "." else null) ?: "") +
            simpleName.asString()
}

fun String?.toProtobufComment(): String =
    if (!isNullOrBlank())
        this.split("\n")
            .dropWhile { it.isBlank() }
            .dropLastWhile { it.isBlank() }
            .joinToString("\n") { "// $it" } + "\n"
    else
        ""


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