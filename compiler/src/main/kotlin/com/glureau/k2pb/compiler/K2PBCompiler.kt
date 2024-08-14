package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.mapping.recordKSClassDeclaration
import com.google.devtools.ksp.common.impl.KSNameImpl
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlinx.serialization.Serializable

// Trick to share the Logger everywhere without injecting the dependency everywhere
internal lateinit var sharedLogger: KSPLogger
internal lateinit var sharedOptions: OptionManager

internal object Logger : KSPLogger by sharedLogger

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
        val symbols = resolver.getSymbolsWithAnnotation(Serializable::class.qualifiedName!!)
        symbols.forEach {
            if (it is KSClassDeclaration) {
                protobufAggregator.recordKSClassDeclaration(it)
            }
        }

        resolveDependencies(resolver)

        protobufAggregator.buildFiles(moduleName(resolver)).forEach { protobufFile ->
            environment.writeProtobufFile(
                protobufFile.toProtoString().toByteArray(),
                fileName = protobufFile.path,
                dependencies = protobufFile.dependencies
            )
        }
        return emptyList()
    }

    private fun resolveDependencies(resolver: Resolver) {
        val lastSignatures = mutableSetOf<String>()
        do {
            var done = true
            val unknownReferences = protobufAggregator.unknownReferences()
            unknownReferences.forEach {
                val reference = resolver.getClassDeclarationByName(KSNameImpl.getCached(it))
                protobufAggregator.recordKSClassDeclaration(requireNotNull(reference))
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