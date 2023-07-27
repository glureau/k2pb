package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.mapping.recordKSClassDeclaration
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.processing.impl.KSNameImpl
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlinx.serialization.Serializable

// Trick to share the Logger everywhere without injecting the dependency everywhere
internal lateinit var sharedLogger: KSPLogger

internal object Logger : KSPLogger by sharedLogger

class K2PBCompiler(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    init {
        sharedLogger = environment.logger
    }

    private val protobufAggregator = ProtobufAggregator()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Serializable::class.qualifiedName!!)
        symbols.forEach {
            if (it is KSClassDeclaration) {
                protobufAggregator.recordKSClassDeclaration(it)
            }
        }
        do {
            var done = true
            protobufAggregator.unknownReferences().forEach {
                val referencedEnum = resolver.getClassDeclarationByName(KSNameImpl.getCached(it))
                Logger.warn("Checking locally unknown reference: $it -> $referencedEnum")
                protobufAggregator.recordKSClassDeclaration(requireNotNull(referencedEnum))
                done = false
            }
        } while (!done)

        protobufAggregator.buildFiles().forEach { protobufFile ->
            environment.writeProtobufFile(
                protobufFile.toString().toByteArray(),
                fileName = protobufFile.path,
                dependencies = protobufFile.dependencies
            )
            Logger.warn("---------------------------- ${protobufFile.path}")
            Logger.warn(protobufFile.toString())
        }

        return emptyList()
    }

}

class K2PBCompilerProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        K2PBCompiler(environment)
}