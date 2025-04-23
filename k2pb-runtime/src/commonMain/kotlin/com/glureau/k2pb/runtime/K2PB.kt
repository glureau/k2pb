package com.glureau.k2pb.runtime

import com.glureau.k2pb.CustomConverter
import com.glureau.k2pb.DelegateProtoCodec
import com.glureau.k2pb.ProtoCodec
import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.ProtobufWriter
import com.glureau.k2pb.runtime.ktx.ByteArrayInput
import com.glureau.k2pb.runtime.ktx.ByteArrayOutput
import com.glureau.k2pb.runtime.ktx.ProtobufReaderImpl
import com.glureau.k2pb.runtime.ktx.ProtobufWriterImpl
import kotlin.reflect.KClass

public class K2PB internal constructor(private val config: K2PBConfig = K2PBConfig()) {
    private val delegated = ConfiguredProtoCodec(config)

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

    public fun getRegisteredChildrenFor(parent: KClass<*>): List<KClass<*>> {
        return config.polymorphics[parent]?.toList() ?: emptyList()
    }

    public fun getProtoMessageName(klass: KClass<*>): String? = config.messageNames[klass]
}

public fun K2PB(configure: K2PBConfig.() -> Unit = {}): K2PB {
    val config = K2PBConfig().apply(configure)
    config.verify()
    return K2PB(config)
}

public class K2PBConfig internal constructor() {
    internal val serializers: MutableMap<KClass<*>, ProtoCodec<*>> = mutableMapOf()
    internal val messageNames: MutableMap<KClass<*>, String> = mutableMapOf()

    private val converters: MutableMap<KClass<*>, CustomConverter<*, *>> = mutableMapOf()

    internal val polymorphics: MutableMap<KClass<*>, MutableList<KClass<*>>> = mutableMapOf()

    public fun registerSerializer(
        typeToSerialize: KClass<*>,
        serializer: ProtoCodec<*>,
        protoMessageName: String
    ) {
        serializers[typeToSerialize] = serializer
        messageNames[typeToSerialize] = protoMessageName
    }

    @Deprecated("Not used YET")
    public fun registerConverter(kClass: KClass<*>, converter: CustomConverter<*, *>) {
        converters[kClass] = converter
    }

    public fun registerPolymorphicChild(parent: KClass<*>, child: KClass<*>) {
        polymorphics.getOrPut(parent) { mutableListOf() }.add(child)
    }

    public fun verify() {
        polymorphics.forEach { (parent, children) ->
            children.forEach { child ->
                if (!serializers.containsKey(child)) {
                    throw IllegalArgumentException("Missing serializer for polymorphic parent: $parent, child: $child")
                }
            }
        }
    }
}

internal class ConfiguredProtoCodec(private val config: K2PBConfig) : DelegateProtoCodec {
    @Suppress("UNCHECKED_CAST")
    override fun ProtobufWriter.encode(instance: Any?, instanceClass: KClass<*>) {
        config.serializers[instanceClass]?.let {
            with(it as ProtoCodec<Any>) {
                encode(instance, this@ConfiguredProtoCodec)
            }
        } ?: throw IllegalArgumentException("Unsupported type: $instanceClass")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> ProtobufReader.decode(instanceClass: KClass<T>): T? {
        config.serializers[instanceClass]?.let {
            with(it as ProtoCodec<Any>) {
                return decode(this@ConfiguredProtoCodec) as? T
            }
        } ?: throw IllegalArgumentException("Unsupported type: $instanceClass")
    }
}


public inline fun <reified T : Any> K2PB.encodeToByteArray(value: T): ByteArray =
    encodeToByteArray(value, T::class)

public inline fun <reified T : Any> K2PB.decodeFromByteArray(data: ByteArray): T? =
    decodeFromByteArray(data, T::class)
