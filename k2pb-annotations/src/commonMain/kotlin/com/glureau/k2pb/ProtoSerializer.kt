package com.glureau.k2pb

import kotlin.reflect.KClass

public interface ProtoSerializer<T> {
    public fun ProtobufWriter.encode(instance: T?, protoSerializer: DelegateProtoSerializer)
    public fun ProtobufReader.decode(protoSerializer: DelegateProtoSerializer): T?
}

public interface DelegateProtoSerializer {
    public fun ProtobufWriter.encode(instance: Any?, instanceClass: KClass<*>)
    public fun <T : Any> ProtobufReader.decode(instanceClass: KClass<T>): T?
}

public interface CustomConverter<Data : Any?, Output : Any?> {
    public fun encode(value: Data): Output
    public fun decode(data: Output): Data?
}

public interface NullableStringConverter<T : Any> : CustomConverter<T, String?>
public interface NullableByteArrayConverter<T : Any> : CustomConverter<T, ByteArray?>
