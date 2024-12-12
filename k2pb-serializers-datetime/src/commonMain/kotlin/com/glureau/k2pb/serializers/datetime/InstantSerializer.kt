package com.glureau.k2pb.serializers.datetime

import com.glureau.k2pb.NullableStringConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat

internal class InstantStringConverter(val format: DateTimeFormat<DateTimeComponents>) :
    NullableStringConverter<Instant> {
    override fun encode(value: Instant): String = value.format(format)

    override fun decode(data: String?): Instant? = data?.let { Instant.parse(data, format) }
}

public class InstantIsoDateTimeOffsetConverter() : NullableStringConverter<Instant>
by InstantStringConverter(DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET)

public class InstantRfc1123Converter() : NullableStringConverter<Instant>
by InstantStringConverter(DateTimeComponents.Formats.RFC_1123)
