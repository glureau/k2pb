package com.glureau.k2pb.runtime

import com.glureau.k2pb.DelegateProtoSerializer
import com.glureau.k2pb.ProtoSerializer
import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.ProtobufWriter
import com.glureau.k2pb.runtime.ktx.ByteArrayInput
import com.glureau.k2pb.runtime.ktx.ByteArrayOutput
import com.glureau.k2pb.runtime.ktx.ProtobufReaderImpl
import com.glureau.k2pb.runtime.ktx.ProtobufWriterImpl
import kotlin.reflect.KClass

class K2PB internal constructor(config: K2PBConfig = K2PBConfig()) {
    private val delegated = ConfiguredProtoSerializer(config)

    fun <T : Any> encodeToByteArray(any: T, klass: KClass<T>): ByteArray {
        val out = ByteArrayOutput()
        val writer: ProtobufWriter = ProtobufWriterImpl(out)
        with(delegated) {
            writer.encode(any, klass)
        }
        return out.toByteArray()
    }

    fun <T : Any> decodeFromByteArray(encoded: ByteArray?, klass: KClass<T>): T? {
        if (encoded == null) return null

        val input = ByteArrayInput(encoded)
        val reader: ProtobufReader = ProtobufReaderImpl(input)
        with(delegated) {
            return reader.decode(klass) as? T
        }
    }
}

fun K2PB(configure: K2PBConfig.() -> Unit = {}): K2PB {
    val config = K2PBConfig().apply(configure)
    return K2PB(config)
}

class K2PBConfig internal constructor() {
    internal val serializers: MutableMap<KClass<*>, ProtoSerializer<*>> = mutableMapOf()
    fun registerSerializer(klass: KClass<*>, serializer: ProtoSerializer<*>) {
        serializers[klass] = serializer
    }
}

internal class ConfiguredProtoSerializer(private val config: K2PBConfig) : DelegateProtoSerializer {
    @Suppress("UNCHECKED_CAST")
    override fun ProtobufWriter.encode(instance: Any?, instanceClass: KClass<*>) {
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


@Suppress("UNCHECKED_CAST")
public inline fun <reified T : Any> K2PB.encodeToByteArray(value: T): ByteArray =
    encodeToByteArray(value, value::class as KClass<T>)

public inline fun <reified T : Any> K2PB.decodeFromByteArray(data: ByteArray): T? =
    decodeFromByteArray(data, T::class)
