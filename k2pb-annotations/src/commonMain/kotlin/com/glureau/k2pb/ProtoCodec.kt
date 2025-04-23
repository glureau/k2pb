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
}

public interface CustomConverter<Data : Any?, Output : Any?> {
    public fun encode(value: Data): Output
    public fun decode(data: Output): Data?
}

public interface NullableStringConverter<T : Any> : CustomConverter<T, String?>
public interface NullableByteArrayConverter<T : Any> : CustomConverter<T, ByteArray?>
