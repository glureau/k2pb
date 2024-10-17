package com.glureau.k2pb.runtime.ktx;

public enum class ProtoIntegerType(internal val signature: Long) {
    DEFAULT(0L shl 33),
    SIGNED(1L shl 33),
    FIXED(2L shl 33);
}
