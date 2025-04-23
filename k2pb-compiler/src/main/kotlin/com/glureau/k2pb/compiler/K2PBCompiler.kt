package com.glureau.k2pb.compiler

import com.glureau.k2pb.annotation.ProtoPolymorphism
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.compiler.mapping.classNamesToOneOfField
import com.glureau.k2pb.compiler.mapping.protoPolymorphismAnnotation
import com.glureau.k2pb.compiler.mapping.recordKSClassDeclaration
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.OneOfField
import com.glureau.k2pb.compiler.struct.emitNullabilityProto
import com.google.devtools.ksp.common.impl.KSNameImpl
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.impl.hasAnnotation
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.Collections.emptyList

// Trick to share the Logger everywhere without injecting the dependency everywhere
internal lateinit var sharedLogger: KSPLogger
internal lateinit var sharedOptions: OptionManager

internal object Logger : KSPLogger by sharedLogger

data class CompileOptions(private val options: Map<String, String>) {
    val protoPackageName by lazy { options["com.glureau.k2pb.protoPackageName"] }
    val javaPackage by lazy { options["com.glureau.k2pb.javaPackage"] }
    val javaOuterClassnameSuffix by lazy { options["com.glureau.k2pb.javaOuterClassnameSuffix"] }
    val emitNullability by lazy { options["com.glureau.k2pb.emitNullability"].toBoolean() }
}

internal lateinit var compileOptions: CompileOptions

class K2PBCompiler(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    init {
        sharedLogger = environment.logger
        sharedOptions = OptionManager(environment.options)
    }

    private val protobufAggregator = ProtobufAggregator()
    private var runDone = false
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (runDone) {
            return emptyList()
        }
        runDone = true
        compileOptions = CompileOptions(environment.options)
        val symbols = resolver.getSymbolsWithAnnotation(ProtoMessage::class.qualifiedName!!)
        symbols.forEach {
            if (it is KSClassDeclaration) {
                protobufAggregator.recordKSClassDeclaration(it)
            }
        }

        resolvePolymorphism(resolver)

        resolveDependencies(resolver)

        val moduleName = moduleName(resolver)
        ProtobufFileProducer(protobufAggregator).buildFiles(moduleName).forEach { protobufFile ->
            environment.writeProtobufFile(
                protobufFile.toProtoString().toByteArray(),
                packageName = compileOptions.protoPackageName?.let { "k2pb.$it" } ?: "k2pb",
                fileName = protobufFile.path,
                dependencies = protobufFile.dependencies
            )
        }

        if (compileOptions.emitNullability) {
            emitNullabilityProto(environment)
        }

        ProtobufSerializerProducer(protobufAggregator).buildFileSpecs(moduleName).forEach { protobufFile ->
            protobufFile.fileSpec.writeTo(environment.codeGenerator, false)
        }
        return emptyList()
    }

    private fun resolvePolymorphism(resolver: Resolver) {
        resolver.getSymbolsWithAnnotation(ProtoPolymorphism::class.qualifiedName!!).forEach { symbol ->
            symbol.protoPolymorphismAnnotation()?.let { annotation ->
                val parentKClass = annotation.getArg<KSType>(ProtoPolymorphism::parent)
                val parent = parentKClass.toClassName()
                val oneOfAnnotations = annotation.getArg<List<KSAnnotation>>(ProtoPolymorphism::oneOf)
                val oneOf = oneOfAnnotations.map {
                    val className = it.getArg<KSType>(ProtoPolymorphism.Child::kClass).toClassName()
                    val number = it.getArg<Int>(ProtoPolymorphism.Child::number)
                    className to number
                }
                val deprecateOneOfAnnotations = annotation.getArg<List<KSAnnotation>>(ProtoPolymorphism::deprecateOneOf)
                val deprecateOneOf = deprecateOneOfAnnotations.map {
                    OneOfField.DeprecatedField(
                        protoName = it.getArg<String>(ProtoPolymorphism.Deprecated::protoName),
                        protoNumber = it.getArg<Int>(ProtoPolymorphism.Deprecated::protoNumber),
                        deprecationReason = it.getArg<String?>(ProtoPolymorphism.Deprecated::deprecationReason),
                        publishedInProto = it.getArg<Boolean?>(ProtoPolymorphism.Deprecated::publishedInProto) ?: true,
                    )
                }
                val protoName = annotation.getArg<String?>(ProtoPolymorphism::name)

                protobufAggregator.recordNode(
                    MessageNode(
                        packageName = parent.packageName,
                        qualifiedName = parent.canonicalName,
                        name = parent.simpleName,
                        protoName = protoName ?: parent.simpleName,
                        isPolymorphic = true,
                        isSealed = false,
                        explicitGenerationRequested = true,
                        isInlineClass = false,
                        superTypes = emptyList(),
                        comment = null, // TODO: Should we just remove that variable?
                        fields = classNamesToOneOfField(
                            fieldName = parent.simpleName,
                            subclassesWithProtoNumber = oneOf,
                            deprecateOneOf = deprecateOneOf,
                        ),
                        originalFile = symbol.containingFile,
                        sealedSubClasses = emptyList(),
                    )
                )
            }
        }
    }

    private fun resolveDependencies(resolver: Resolver) {
        val lastSignatures = mutableSetOf<String>()
        do {
            var done = true
            val unknownReferences = protobufAggregator.unknownReferences()
            unknownReferences
                //.filter { it != nullabilityQualifiedName }
                .forEach {
                    val reference = resolver.getClassDeclarationByName(KSNameImpl.getCached(it))!!
                    if (!reference.hasAnnotation(ProtoMessage::class.qualifiedName!!)) {
                        Logger.warn("$it is referenced but not annotated with @ProtoMessage")
                        // TODO: Should be an error?
                    }

                    TypeResolver.qualifiedNameToProtobufName[reference.qualifiedName!!.asString()] =
                            //(compileOptions.protoPackageName?.let { "$it." } ?: "") +
                        reference.simpleName.asString()
                    //protobufAggregator.recordKSClassDeclaration(requireNotNull(reference))
                    done = false
                }

            if (!done && lastSignatures.isNotEmpty() && lastSignatures == unknownReferences) {
                Logger.warn("Cannot resolve the following references: $unknownReferences")
                done = true // Need to stop the processor, otherwise it will loop forever.
            } else {
                lastSignatures.clear()
                lastSignatures.addAll(unknownReferences)
            }
        } while (!done)
    }
}

/**
 * todo use `resolver.moduleName` when [https://github.com/google/ksp/issues/1015] is done
 * @return gradle module name
 */
private fun moduleName(resolver: Resolver): String {
    val moduleDescriptor = resolver::class.java
        .getDeclaredField("module")
        .apply { isAccessible = true }
        .get(resolver)
    val rawName = moduleDescriptor::class.java
        .getMethod("getName")
        .invoke(moduleDescriptor)
        .toString()
    return rawName.removeSurrounding("<", ">")
        // TODO: should we ignore all KMP possible suffixes?
        .removeSuffix("_commonMain")
}

class K2PBCompilerProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): K2PBCompiler = K2PBCompiler(environment)
}