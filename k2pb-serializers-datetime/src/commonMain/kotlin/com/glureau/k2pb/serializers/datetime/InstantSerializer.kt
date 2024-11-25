package com.glureau.k2pb.serializers.datetime

import com.glureau.k2pb.CustomStringConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat

internal class InstantStringConverter(val format: DateTimeFormat<DateTimeComponents>) :
    CustomStringConverter<Instant> {
    override fun encode(value: Instant): String = value.format(format)

    override fun decode(data: String): Instant? = runCatching {
        Instant.parse(data, format)
    }.getOrNull()
}

public class InstantIsoDateTimeOffsetConverter() : CustomStringConverter<Instant>
by InstantStringConverter(DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET)

public class InstantRfc1123Converter() : CustomStringConverter<Instant>
by InstantStringConverter(DateTimeComponents.Formats.RFC_1123)
