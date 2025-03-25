package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoField
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.serializers.datetime.InstantIsoDateTimeOffsetConverter
import kotlinx.datetime.Instant

@ProtoMessage(name = "CustomNameKtxClasses")
data class KtxClasses(
    @ProtoField(converter = InstantIsoDateTimeOffsetConverter::class)
    val instant: Instant,
)