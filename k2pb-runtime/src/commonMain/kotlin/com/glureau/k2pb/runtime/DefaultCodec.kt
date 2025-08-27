package com.glureau.k2pb.runtime

import com.glureau.k2pb.DefaultCodec
import com.glureau.k2pb.DelegateProtoCodec
import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.runtime.ktx.ByteArrayInput
import com.glureau.k2pb.runtime.ktx.ProtobufReaderImpl
import kotlin.reflect.KClass

public class DefaultCodecImpl(private val delegateProtoCodec: DelegateProtoCodec) : DefaultCodec {
    public override fun encode(instance: Any?, instanceClass: KClass<*>): ByteArray {
        return protobufWriter {
            with(delegateProtoCodec) {
                encode(instance, instanceClass)
            }
        }
    }

    override fun <T : Any> decode(byteArray: ByteArray, instanceClass: KClass<T>): T? {
        val input = ByteArrayInput(byteArray)
        val reader: ProtobufReader = ProtobufReaderImpl(input)
        return with(delegateProtoCodec) {
            reader.decode(instanceClass)
        }
    }
}
