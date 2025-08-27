package com.glureau.sample

import com.glureau.k2pb.NullableStringConverter
import com.glureau.k2pb.annotation.ProtoField
import com.glureau.k2pb.annotation.ProtoMessage
import java.math.BigDecimal


class BigDecimalConverter : NullableStringConverter<BigDecimal> {
    override fun encode(value: BigDecimal): String? =
        value.toPlainString()

    override fun decode(data: String?): BigDecimal? =
        data?.takeIf { it.isNotBlank() }?.toBigDecimal()
}

// Here the compiler cannot infer the replacement type (KSP only give access to signatures, not runtime information)
// So we need to add an argument in the build.gradle file.
// (note that the @Contextual annotation could also be used and the mapping define multiple times  in other modules
//  with different serializer and different type used, so we cannot extract just 1 proto file in theory)
@ProtoMessage
data class BigDecimalHolder(@ProtoField(converter = BigDecimalConverter::class) val bd: BigDecimal)

@ProtoMessage
data class NullableBigDecimalHolder(@ProtoField(converter = BigDecimalConverter::class) val bd: BigDecimal?)

@JvmInline
@ProtoMessage
value class BigDecimalValueClass(@ProtoField(converter = BigDecimalConverter::class) val bd: BigDecimal)

@ProtoMessage
data class BigDecimalValueClassHolder(val bdValue: BigDecimalValueClass)

@JvmInline
@ProtoMessage
value class NullableBigDecimalValueClass(@ProtoField(converter = BigDecimalConverter::class) val bd: BigDecimal?)

@ProtoMessage
data class NullableBigDecimalValueClassHolder(val nullableBdValue: NullableBigDecimalValueClass)
