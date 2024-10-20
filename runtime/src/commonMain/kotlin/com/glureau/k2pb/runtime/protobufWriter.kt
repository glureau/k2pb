package com.glureau.k2pb.runtime

import com.glureau.k2pb.ProtobufWriter
import com.glureau.k2pb.runtime.ktx.ByteArrayOutput
import com.glureau.k2pb.runtime.ktx.ProtobufWriterImpl

fun protobufWriter(act: ProtobufWriter.() -> Unit): ByteArray {
    val out = ByteArrayOutput()
    ProtobufWriterImpl(out).act()
    return out.toByteArray()
}

fun ProtobufWriter.writeMessage(tag: Int, act: ProtobufWriter.() -> Unit) {
    val out = ByteArrayOutput()
    ProtobufWriterImpl(out).act()
    writeBytes(out.toByteArray(), tag)
}
