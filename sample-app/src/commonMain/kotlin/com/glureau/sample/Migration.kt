package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.annotation.ProtoMigration
import com.glureau.k2pb.annotation.UnspecifiedBehavior
import com.glureau.sample.lib.AnEnum


@ProtoMessage
data class NullableEnumHolderUnspecifiedNull(
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val enum: AnEnum?,
)

@ProtoMessage
data class NullableEnumHolderUnspecifiedDefault(
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val enum: AnEnum?,
)

@ProtoMessage
data class NullableNativeTypeEventUnspecifiedNull(
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val integer: Int?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val long: Long?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val float: Float?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val double: Double?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val string: String?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val short: Short?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val char: Char?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val boolean: Boolean?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val byte: Byte?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val byteArray: ByteArray?,
) : EventInterface {

    // ByteArray requires to generate equals & hashcode, as data class doesn't compare ByteArray content
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NullableNativeTypeEventUnspecifiedNull

        if (integer != other.integer) return false
        if (long != other.long) return false
        if (float != other.float) return false
        if (double != other.double) return false
        if (short != other.short) return false
        if (char != other.char) return false
        if (boolean != other.boolean) return false
        if (byte != other.byte) return false
        if (string != other.string) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = integer ?: 0
        result = 31 * result + (long?.hashCode() ?: 0)
        result = 31 * result + (float?.hashCode() ?: 0)
        result = 31 * result + (double?.hashCode() ?: 0)
        result = 31 * result + (short ?: 0)
        result = 31 * result + (char?.hashCode() ?: 0)
        result = 31 * result + (boolean?.hashCode() ?: 0)
        result = 31 * result + (byte ?: 0)
        result = 31 * result + (string?.hashCode() ?: 0)
        result = 31 * result + (byteArray?.contentHashCode() ?: 0)
        return result
    }

}

@ProtoMessage
data class NullableNativeTypeEventUnspecifiedDefault(
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val integer: Int?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val long: Long?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val float: Float?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val double: Double?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val string: String?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val short: Short?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val char: Char?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val boolean: Boolean?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val byte: Byte?,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val byteArray: ByteArray?,
) : EventInterface {

    // ByteArray requires to generate equals & hashcode, as data class doesn't compare ByteArray content
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NullableNativeTypeEventUnspecifiedDefault

        if (integer != other.integer) return false
        if (long != other.long) return false
        if (float != other.float) return false
        if (double != other.double) return false
        if (short != other.short) return false
        if (char != other.char) return false
        if (boolean != other.boolean) return false
        if (byte != other.byte) return false
        if (string != other.string) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = integer ?: 0
        result = 31 * result + (long?.hashCode() ?: 0)
        result = 31 * result + (float?.hashCode() ?: 0)
        result = 31 * result + (double?.hashCode() ?: 0)
        result = 31 * result + (short ?: 0)
        result = 31 * result + (char?.hashCode() ?: 0)
        result = 31 * result + (boolean?.hashCode() ?: 0)
        result = 31 * result + (byte ?: 0)
        result = 31 * result + (string?.hashCode() ?: 0)
        result = 31 * result + (byteArray?.contentHashCode() ?: 0)
        return result
    }
}

@ProtoMessage
data class NativeTypeEventUnspecifiedDefault(
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val integer: Int,
    @ProtoMigration(unspecified = UnspecifiedBehavior.NULL) val long: Long,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val float: Float,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val double: Double,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val string: String,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val short: Short,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val char: Char,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val boolean: Boolean,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val byte: Byte,
    @ProtoMigration(unspecified = UnspecifiedBehavior.DEFAULT) val byteArray: ByteArray,
) : EventInterface {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NativeTypeEventUnspecifiedDefault

        if (integer != other.integer) return false
        if (long != other.long) return false
        if (float != other.float) return false
        if (double != other.double) return false
        if (short != other.short) return false
        if (char != other.char) return false
        if (boolean != other.boolean) return false
        if (byte != other.byte) return false
        if (string != other.string) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = integer
        result = 31 * result + long.hashCode()
        result = 31 * result + float.hashCode()
        result = 31 * result + double.hashCode()
        result = 31 * result + short
        result = 31 * result + char.hashCode()
        result = 31 * result + boolean.hashCode()
        result = 31 * result + byte
        result = 31 * result + string.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}
