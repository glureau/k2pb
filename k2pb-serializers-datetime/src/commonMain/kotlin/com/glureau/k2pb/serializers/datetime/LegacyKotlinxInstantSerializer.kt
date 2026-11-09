@file:Suppress("DEPRECATION")

package com.glureau.k2pb.serializers.datetime

import com.glureau.k2pb.NullableStringConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat

internal class LegacyKotlinxInstantStringConverter(val format: DateTimeFormat<DateTimeComponents>) :
    NullableStringConverter<Instant> {
    override fun encode(value: Instant): String = value.format(format)

    override fun decode(data: String?): Instant? = data?.let { Instant.parse(data, format) }
}

public class LegacyKotlinxInstantIsoDateTimeOffsetConverter() : NullableStringConverter<Instant>
by LegacyKotlinxInstantStringConverter(DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET)

public class LegacyKotlinxInstantRfc1123Converter() : NullableStringConverter<Instant>
by LegacyKotlinxInstantStringConverter(DateTimeComponents.Formats.RFC_1123)
