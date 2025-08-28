package com.glureau.sample

import com.glureau.k2pb.annotation.DeprecatedField
import com.glureau.k2pb.annotation.DeprecatedNullabilityField
import com.glureau.k2pb.annotation.NullabilityMigration
import com.glureau.k2pb.annotation.ProtoField
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.lib.AnEnum


@ProtoMessage
data class NullableEnumHolderUnspecifiedNull(
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val enum: AnEnum?,
)

@ProtoMessage
data class NullableEnumHolderUnspecifiedDefault(
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val enum: AnEnum?,
)

@ProtoMessage
data class NullableNativeTypeEventUnspecifiedNull(
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val integer: Int?,
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val long: Long?,
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val float: Float?,
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val double: Double?,
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val string: String?,
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val short: Short?,
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val char: Char?,
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val boolean: Boolean?,
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val byte: Byte?,
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val byteArray: ByteArray?,
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
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val integer: Int?,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val long: Long?,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val float: Float?,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val double: Double?,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val string: String?,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val short: Short?,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val char: Char?,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val boolean: Boolean?,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val byte: Byte?,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val byteArray: ByteArray?,
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
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val integer: Int,
    @ProtoField(nullabilityMigration = NullabilityMigration.NULL) val long: Long,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val float: Float,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val double: Double,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val string: String,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val short: Short,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val char: Char,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val boolean: Boolean,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val byte: Byte,
    @ProtoField(nullabilityMigration = NullabilityMigration.DEFAULT) val byteArray: ByteArray,
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

@ProtoMessage
data class OptionalToRequiredEnumStart(
    val enum: AnEnum?,
    val b: String,
)

@ProtoMessage(
    deprecatedFields = [
        DeprecatedField(
            protoName = "anotherField",
            protoNumber = 5,
            deprecationReason = "Field 'enum' has been made required",
            publishedInProto = false
        ),
    ],
    deprecatedNullabilityFields = [
        DeprecatedNullabilityField(
            protoName = "enum",
            protoNumber = 2,
            deprecationReason = "Field 'enum' has been made required",
            publishedInProto = true
        ),
    ]
)
data class OptionalToRequiredEnumEnd(
    val enum: AnEnum,
    val b: String,
)

@ProtoMessage
data class OptionalToRequiredStart(
    val item: CommonClass?,
    val b: String,
)

@ProtoMessage(
    deprecatedFields = [
        DeprecatedField(
            protoName = "anotherField",
            protoNumber = 5,
            deprecationReason = "Field 'enum' has been made required",
            publishedInProto = false
        ),
    ],
)
data class OptionalToRequiredEnd(
    // Decoding a null value will throw an exception.
    val item: CommonClass,
    val b: String,
)

@ProtoMessage
data class RequiredToOptionalEnumStart(
    val enum: AnEnum,
    val b: String,
)

@ProtoMessage
data class RequiredToOptionalEnumEnd(
    @ProtoField(nullabilityNumber = 3)
    val enum: AnEnum?,
    val b: String,
)

@ProtoMessage
data class RequiredToOptionalStart(
    val item: CommonClass,
    val b: String,
)

@ProtoMessage
data class RequiredToOptionalEnd(
    val item: CommonClass?,
    val b: String,
)
