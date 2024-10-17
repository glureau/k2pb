package com.glureau.k2pb

import com.glureau.k2pb.annotation.ProtobufWriter

interface ProtoSerializer<T> {
    fun ProtobufWriter.encode(instance: T, delegate: DelegateProtoSerializer)
    fun decode(data: ByteArray, delegate: DelegateProtoSerializer): T?
}

interface DelegateProtoSerializer {
    fun ProtobufWriter.encode(instance: Any)
    fun decode(data: ByteArray): Any?
}