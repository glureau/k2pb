package com.glureau.k2pb.compiler

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlinx.serialization.Serializable

class FailingCompiler(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Serializable::class.qualifiedName!!)
        val klassDeclaration = symbols.toList()[0] as KSClassDeclaration
        val params = klassDeclaration.primaryConstructor!!.parameters
        val resolved = params[0].type.resolve()
        if (resolved.isError) {
            environment.logger.error("RESOLVED: $resolved")
        }
        return emptyList()
    }
}

