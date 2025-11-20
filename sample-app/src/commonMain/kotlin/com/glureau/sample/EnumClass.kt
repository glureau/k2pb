package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoField
import com.glureau.k2pb.annotation.ProtoMessage

@ProtoMessage
enum class EnumClass {
    A,
    B,
    @ProtoField("Colibri")
    C,
}