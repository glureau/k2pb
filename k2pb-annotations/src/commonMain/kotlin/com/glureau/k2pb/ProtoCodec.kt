package com.glureau.k2pb

import kotlin.reflect.KClass

public interface ProtoEncoder<T> {
    public fun ProtobufWriter.encode(instance: T?, protoSerializer: DelegateProtoCodec)
}

public interface ProtoDecoder<T> {
    public fun ProtobufReader.decode(protoSerializer: DelegateProtoCodec): T?
}

public interface ProtoCodec<T> : ProtoEncoder<T>, ProtoDecoder<T>

public interface DelegateProtoCodec {
    public fun ProtobufWriter.encode(instance: Any?, instanceClass: KClass<*>)
    public fun <T : Any> ProtobufReader.decode(instanceClass: KClass<T>): T?
    public fun onUnknownProtoNumber(instanceClass: KClass<*>, protoNumber: Int)
}

public interface DefaultCodec {
    public fun encode(instance: Any?, instanceClass: KClass<*>): ByteArray
    public fun <T : Any> decode(byteArray: ByteArray, instanceClass: KClass<T>): T?
}

public interface CustomConverter<Data : Any?, Output : Any?> {
    public fun encode(value: Data, defaultCodec: DefaultCodec): Output
    public fun decode(data: Output, defaultCodec: DefaultCodec): Data?
}

public interface NullableStringConverter<T : Any> : CustomConverter<T, String?> {
    override fun encode(value: T, defaultCodec: DefaultCodec): String? {
        return encode(value)
    }

    public fun encode(value: T): String?

    override fun decode(data: String?, defaultCodec: DefaultCodec): T? {
        return if (data == null) null else decode(data)
    }

    public fun decode(data: String?): T?
}

public interface NullableByteArrayConverter<T : Any> : CustomConverter<T, ByteArray?>
