package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage


@ProtoMessage
data class WithNestClassA(
    val a: NestedClass = NestedClass("nested A"),
) {
    @ProtoMessage
    data class NestedClass(
        val nested: String = "nested",
    )
}

@ProtoMessage
data class WithNestClassB(
    val b: NestedClass = NestedClass(),
) {
    @ProtoMessage
    data class NestedClass(
        val nestedEnum: NestedEnum = NestedEnum.C,
    ) {
        @ProtoMessage
        enum class NestedEnum {
            A, B, C
        }
    }
}


@ProtoMessage
data class WithNestedEnum(
    val enum: NestedEnum2 = NestedEnum2.C,
) {
    enum class NestedEnum2 {
        A, B, C
    }
}