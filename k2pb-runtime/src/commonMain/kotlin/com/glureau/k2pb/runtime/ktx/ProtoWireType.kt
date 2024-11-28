package com.glureau.k2pb.runtime.ktx;

public enum class ProtoWireType(public val typeId: Int) {
    INVALID(-1),
    VARINT(0),
    i64(1),
    SIZE_DELIMITED(2),
    i32(5),
    ;

    public companion object {
        public fun from(typeId: Int): ProtoWireType {
            return ProtoWireType.entries.find { it.typeId == typeId } ?: INVALID
        }
    }

    public fun wireIntWithTag(tag: Int): Int {
        return ((tag shl 3) or typeId)
    }

    override fun toString(): String {
        return "${this.name}($typeId)"
    }
}