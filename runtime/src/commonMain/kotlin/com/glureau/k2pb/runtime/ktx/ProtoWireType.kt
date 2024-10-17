package com.glureau.k2pb.runtime.ktx;

internal enum class ProtoWireType(val typeId: Int) {
    INVALID(-1),
    VARINT(0),
    i64(1),
    SIZE_DELIMITED(2),
    i32(5),
    ;

    companion object {
        fun from(typeId: Int): ProtoWireType {
            return ProtoWireType.entries.find { it.typeId == typeId } ?: INVALID
        }
    }

    fun wireIntWithTag(tag: Int): Int {
        return ((tag shl 3) or typeId)
    }

    override fun toString(): String {
        return "${this.name}($typeId)"
    }
}