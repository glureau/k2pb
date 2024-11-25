package com.glureau.k2pb.runtime.ktx

public class SerializationException(message: String?) : IllegalArgumentException(message)

public class ProtobufDecodingException(message: String?) : IllegalArgumentException(message)
