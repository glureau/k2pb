package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoField
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.runtime.UuidBytesConverter
import com.glureau.k2pb.runtime.UuidStringConverter
import kotlin.uuid.Uuid

@ProtoMessage
data class UuidsHolder(
    @ProtoField(converter = UuidStringConverter::class) val uuidAsString: Uuid,
    @ProtoField(converter = UuidBytesConverter::class) val uuidAsBytes: Uuid,
    val stringValueClass: UuidStringValueClass,
    val bytesValueClass: UuidBytesValueClass,
)

@ProtoMessage
data class NullableUuidsHolder(
    @ProtoField(converter = UuidStringConverter::class) val uuidAsString: Uuid?,
    @ProtoField(converter = UuidBytesConverter::class) val uuidAsBytes: Uuid?,
    val stringValueClass: UuidStringValueClass?,
    val bytesValueClass: UuidBytesValueClass?,
)


@JvmInline
@ProtoMessage
value class UuidStringValueClass(
    @ProtoField(converter = UuidStringConverter::class) val uuidAsString: Uuid,
)

@JvmInline
@ProtoMessage
value class UuidBytesValueClass(
    @ProtoField(converter = UuidBytesConverter::class) val uuidAsString: Uuid,
)
