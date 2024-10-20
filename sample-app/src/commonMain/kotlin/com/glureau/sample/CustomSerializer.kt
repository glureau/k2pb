package com.glureau.sample

import com.glureau.k2pb.CustomStringSerializer
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.annotation.ProtoStringSerializer
import java.math.BigDecimal


class BigDecimalSerializer : CustomStringSerializer<BigDecimal> {
    override fun encode(value: BigDecimal): String = value.toPlainString() ?: "0.0"

    override fun decode(data: String): BigDecimal = data.takeIf { it.isNotBlank() }?.toBigDecimal()!!
}

// Here the compiler cannot infer the replacement type (KSP only give access to signatures, not runtime information)
// So we need to add an argument in the build.gradle file.
// (note that the @Contextual annotation could also be used and the mapping define multiple times  in other modules
//  with different serializer and different type used, so we cannot extract just 1 proto file in theory)
@ProtoMessage
data class BigDecimalHolder(@ProtoStringSerializer(BigDecimalSerializer::class) val bd: BigDecimal)

@JvmInline
@ProtoMessage
value class BigDecimalValueClass(@ProtoStringSerializer(BigDecimalSerializer::class) val bd: BigDecimal)

@ProtoMessage
data class BigDecimalValueClassHolder(val bdValue: BigDecimalValueClass)
