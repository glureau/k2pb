package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoField
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.serializers.datetime.InstantIsoDateTimeOffsetConverter
import com.glureau.k2pb.serializers.datetime.LegacyKotlinxInstantIsoDateTimeOffsetConverter
import kotlin.time.Instant

@ProtoMessage(name = "CustomNameKtxClasses")
data class KtxClasses(
    @ProtoField(converter = InstantIsoDateTimeOffsetConverter::class)
    val instant: Instant,
    @ProtoField(converter = LegacyKotlinxInstantIsoDateTimeOffsetConverter::class)
    val legacyInstant: kotlinx.datetime.Instant,
)

// Ensures the generated protobuf file uses the correct name
@ProtoMessage
data class KtxClassesHolder(
    val delegated: KtxClasses,
)
