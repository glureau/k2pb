package com.glureau.k2pb.runtime

import com.glureau.k2pb.CustomConverter
import com.glureau.k2pb.DelegateProtoSerializer
import com.glureau.k2pb.ProtoSerializer
import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.ProtobufWriter
import com.glureau.k2pb.runtime.ktx.ByteArrayInput
import com.glureau.k2pb.runtime.ktx.ByteArrayOutput
import com.glureau.k2pb.runtime.ktx.ProtobufReaderImpl
import com.glureau.k2pb.runtime.ktx.ProtobufWriterImpl
import kotlin.reflect.KClass

public class K2PB internal constructor(config: K2PBConfig = K2PBConfig()) {
    private val delegated = ConfiguredProtoSerializer(config)

    public fun <T : Any> encodeToByteArray(any: T, klass: KClass<T>): ByteArray {
        val out = ByteArrayOutput()
        val writer: ProtobufWriter = ProtobufWriterImpl(out)
        with(delegated) {
            writer.encode(any, klass)
        }
        return out.toByteArray()
    }

    public fun <T : Any> decodeFromByteArray(encoded: ByteArray?, klass: KClass<T>): T? {
        if (encoded == null) return null

        val input = ByteArrayInput(encoded)
        val reader: ProtobufReader = ProtobufReaderImpl(input)
        with(delegated) {
            return reader.decode(klass) as? T
        }
    }
}

public fun K2PB(configure: K2PBConfig.() -> Unit = {}): K2PB {
    val config = K2PBConfig().apply(configure)
    config.verify()
    return K2PB(config)
}

public class K2PBConfig internal constructor() {
    @PublishedApi
    internal val serializers: MutableMap<KClass<*>, ProtoSerializer<*>> = mutableMapOf()

    @PublishedApi
    internal val converters: MutableMap<KClass<*>, CustomConverter<*, *>> = mutableMapOf()

    @PublishedApi
    internal val polymorphics: MutableMap<KClass<*>, MutableList<KClass<*>>> = mutableMapOf()

    @PublishedApi
    internal val polymorphicDefinitions: MutableMap<KClass<*>, Map<KClass<*>, Int>> = mutableMapOf()

    public inline fun registerSerializer(kClass: KClass<*>, serializer: ProtoSerializer<*>) {
        serializers[kClass] = serializer
    }

    public inline fun registerConverter(kClass: KClass<*>, converter: CustomConverter<*, *>) {
        converters[kClass] = converter
    }

    public inline fun registerPolymorphicParent(kClass: KClass<*>) {
        polymorphics += kClass to mutableListOf()
    }

    public inline fun registerPolymorphicChild(parentKClass: KClass<*>, childKClass: KClass<*>) {
        polymorphics.getOrPut(parentKClass, { mutableListOf() }).add(childKClass)
    }

    public inline fun registerPolymorphicDefinition(parentKClass: KClass<*>, children: Map<KClass<*>, Int>) {
        polymorphicDefinitions += parentKClass to children
        /*children.keys.all {
            it.qualifiedName == "a"
        }*/
    }

    public fun verify() {
        polymorphics.forEach { (parent, children) ->
            children.forEach { child ->
                if (!serializers.containsKey(child)) {
                    throw IllegalArgumentException("Missing serializer for polymorphic child: $child")
                }
            }
        }
    }
}

internal class ConfiguredProtoSerializer(private val config: K2PBConfig) : DelegateProtoSerializer {
    @Suppress("UNCHECKED_CAST")
    override fun ProtobufWriter.encode(instance: Any?, instanceClass: KClass<*>) {
        println("ConfiguredProtoSerializer - encode - $instanceClass")
        config.serializers[instanceClass]?.let {
            with(it as ProtoSerializer<Any>) {
                encode(instance, this@ConfiguredProtoSerializer)
            }
        } ?: throw IllegalArgumentException("Unsupported type: $instanceClass")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> ProtobufReader.decode(instanceClass: KClass<T>): T? {
        config.serializers[instanceClass]?.let {
            with(it as ProtoSerializer<Any>) {
                return decode(this@ConfiguredProtoSerializer) as? T
            }
        } ?: throw IllegalArgumentException("Unsupported type: $instanceClass")
    }
}


public inline fun <reified T : Any> K2PB.encodeToByteArray(value: T): ByteArray =
    encodeToByteArray(value, T::class)

public inline fun <reified T : Any> K2PB.decodeFromByteArray(data: ByteArray): T? =
    decodeFromByteArray(data, T::class)
