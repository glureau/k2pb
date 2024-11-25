package com.glureau.k2pb.runtime

import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.ProtobufWriter
import com.glureau.k2pb.runtime.ktx.ByteArrayInput
import com.glureau.k2pb.runtime.ktx.ByteArrayOutput
import com.glureau.k2pb.runtime.ktx.ProtobufReaderImpl
import com.glureau.k2pb.runtime.ktx.ProtobufWriterImpl

public fun protobufWriter(act: ProtobufWriter.() -> Unit): ByteArray {
    val out = ByteArrayOutput()
    ProtobufWriterImpl(out).act()
    return out.toByteArray()
}

public fun ProtobufWriter.writeMessage(tag: Int, act: ProtobufWriter.() -> Unit) {
    val out = ByteArrayOutput()
    ProtobufWriterImpl(out).act()
    writeBytes(out.toByteArray(), tag)
}

public fun <T> ProtobufReader.readMessage(act: ProtobufReader.() -> T): T {
    val data = readByteArray()
    val input = ByteArrayInput(data)
    return ProtobufReaderImpl(input).act()
}
