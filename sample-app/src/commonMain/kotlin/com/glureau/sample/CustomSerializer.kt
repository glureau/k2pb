package com.glureau.sample

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

class CustomSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Decimal", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toPlainString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return decoder.decodeString().toBigDecimal()
    }
}

// Here the compiler cannot infer the replacement type (KSP only give access to signatures, not runtime information)
// So we need to add an argument in the build.gradle file.
// (note that the @Contextual annotation could also be used and the mapping define multiple times  in other modules
//  with different serializer and different type used, so we cannot extract just 1 proto file in theory)
@Serializable
data class BigDecimalHolder(@Serializable(CustomSerializer::class) val bd: BigDecimal)