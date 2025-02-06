package com.glureau.k2pb

public interface ProtoConstructor<T> {
    public fun create(parameters: Map<String, Any?>): T
}