package com.glureau.k2pb.compiler.struct

enum class ScalarType : FieldType { // https://protobuf.dev/programming-guides/proto3/#scalar
    double,
    float,
    int32,
    int64,
    uint32,
    uint64,
    sint32,
    sint64,
    fixed32,
    fixed64,
    sfixed32,
    sfixed64,
    bool,
    string,
    bytes,
}

fun StringBuilder.appendScalarType(type: ScalarType) {
    append(type.name)
}