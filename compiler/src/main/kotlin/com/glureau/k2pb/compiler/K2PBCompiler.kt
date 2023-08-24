package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.mapping.recordKSClassDeclaration
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.processing.impl.KSNameImpl
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
        if (runDone) return emptyList()
        runDone = true
        val symbols = resolver.getSymbolsWithAnnotation(Serializable::class.qualifiedName!!)
        symbols.forEach {
            if (it is KSClassDeclaration) {
                protobufAggregator.recordKSClassDeclaration(it)
            }
        }
        do {
            var done = true
            protobufAggregator.unknownReferences().forEach {
                val reference = resolver.getClassDeclarationByName(KSNameImpl.getCached(it))
                Logger.info("Unknown references to resolve: $it => $reference")
                protobufAggregator.recordKSClassDeclaration(requireNotNull(reference))
                done = false
            }
        } while (!done)
        protobufAggregator.buildFiles(moduleName(resolver)).forEach { protobufFile ->
            environment.writeProtobufFile(
                protobufFile.toString().toByteArray(),
                fileName = protobufFile.path,
                dependencies = protobufFile.dependencies
            )
            Logger.warn("---------------------------- ${protobufFile.path}")
            //Logger.warn(protobufFile.toString())
        }

        return emptyList()
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
}

class K2PBCompilerProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        K2PBCompiler(environment)
}