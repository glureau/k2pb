package com.glureau.k2pb.runtime

import com.glureau.k2pb.DelegateProtoSerializer
import com.glureau.k2pb.ProtoSerializer
import com.glureau.k2pb.annotation.ProtobufWriter
import com.glureau.k2pb.runtime.ktx.ByteArrayOutput
import com.glureau.k2pb.runtime.ktx.ProtobufWriterImpl
import kotlin.reflect.KClass

class K2PB internal constructor(config: K2PBConfig = K2PBConfig()) {

    fun <T : Any> encodeToByteArray(any: T, klass: KClass<T>): ByteArray {
        val out = ByteArrayOutput()
        val writer = ProtobufWriterImpl(out)

        return out.toByteArray()
    }

    inline fun <reified T> decodeFromByteArray(encoded: ByteArray?): T {
        T::class
        TODO()
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
    override fun ProtobufWriter.encode(instance: Any) {
        config.serializers[instance::class]?.let {
            with(it as ProtoSerializer<Any>) {
                encode(instance, this@ConfiguredProtoSerializer)
            }
        } ?: throw IllegalArgumentException("Unsupported type: ${instance::class}")
    }

    override fun decode(data: ByteArray): Any? {
        TODO("Not yet implemented")
    }

}


@Suppress("UNCHECKED_CAST")
public inline fun <reified T : Any> K2PB.encodeToByteArray(value: T): ByteArray =
    encodeToByteArray(value, value::class as KClass<T>)
