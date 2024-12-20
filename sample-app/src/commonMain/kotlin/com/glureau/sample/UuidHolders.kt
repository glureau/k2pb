package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoConverter
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.runtime.UuidBytesConverter
import com.glureau.k2pb.runtime.UuidStringConverter
import kotlin.uuid.Uuid

@ProtoMessage
data class UuidsHolder(
    @ProtoConverter(UuidStringConverter::class) val uuidAsString: Uuid,
    @ProtoConverter(UuidBytesConverter::class) val uuidAsBytes: Uuid,
)
