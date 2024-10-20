package com.glureau.k2pb

import kotlin.reflect.KClass

interface ProtoSerializer<T> {
    fun ProtobufWriter.encode(instance: T?, delegate: DelegateProtoSerializer)
    fun ProtobufReader.decode(delegate: DelegateProtoSerializer): T?
}

interface DelegateProtoSerializer {
    fun ProtobufWriter.encode(instance: Any?, instanceClass: KClass<*>)
    fun <T : Any> ProtobufReader.decode(instanceClass: KClass<T>): T?
}

interface CustomSerializer<Data : Any, Output : Any> {
    fun encode(value: Data): Output
    fun decode(data: Output): Data?
}

interface CustomStringSerializer<T : Any> : CustomSerializer<T, String>

