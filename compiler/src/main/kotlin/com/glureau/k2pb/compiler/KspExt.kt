package com.glureau.k2pb.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.protobuf.ProtoNumber
import java.io.OutputStream
import kotlin.reflect.KProperty1

fun SymbolProcessorEnvironment.writeProtobufFile(
    content: ByteArray,
    packageName: String = "",
    fileName: String,
    dependencies: List<KSFile>,
) {
    try {
        codeGenerator.createNewFile(
            Dependencies(true, *dependencies.distinctBy { it.filePath }.toTypedArray()),
            packageName,
            fileName,
            "proto"
        ).use {
            it.write(content)
            it.close()
        }
    } catch (faee: FileAlreadyExistsException) {
        // Not sure why yet, to be investigated
    }
}

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

@Suppress("UNCHECKED_CAST")
fun <T> KSAnnotation.getArg(kProp: KProperty1<*, *>) =
    arguments.firstOrNull { it.name?.asString() == kProp.name }?.value as T

fun KSAnnotation.argFrom(kProp: KProperty1<*, *>): KSValueArgument =
    arguments.first {
        it.name?.asString() == kProp.name
    }

@Suppress("UNCHECKED_CAST")
fun KSAnnotation.classesFrom(kProp: KProperty1<*, *>): List<KSType> =
    (argFrom(kProp).value as? List<KSType>).orEmpty()

val KSClassDeclaration.serialName: String
    get() = serialNameInternal ?: simpleName.asString()

val KSPropertyDeclaration.serialName: String
    get() = serialNameInternal ?: simpleName.asString()

private val KSAnnotated.serialNameInternal: String?
    get() =
        annotations.toList()
            .firstOrNull { it.shortName.asString() == SerialName::class.simpleName }
            ?.getArg<String>(SerialName::value)

@OptIn(ExperimentalSerializationApi::class)
val KSPropertyDeclaration.protoNumber: Int?
    get() =
        annotations.toList()
            .firstOrNull { it.shortName.asString() == ProtoNumber::class.simpleName }
            ?.getArg<Int>(ProtoNumber::number)