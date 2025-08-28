@file:OptIn(ExperimentalUuidApi::class)

package com.glureau.k2pb.runtime

import com.glureau.k2pb.DefaultCodec
import com.glureau.k2pb.NullableByteArrayConverter
import com.glureau.k2pb.NullableStringConverter
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Optimized converter for [Uuid] to/from [ByteArray].
 */
public class UuidBytesConverter : NullableByteArrayConverter<Uuid> {
    override fun encode(value: Uuid, defaultCodec: DefaultCodec): ByteArray? =
        value.toByteArray()

    override fun decode(data: ByteArray?, defaultCodec: DefaultCodec): Uuid? =
        data?.let { Uuid.fromByteArray(it) }
}

/**
 * This converter can be useful for debugging, if the message size is not a concern,
 * or due to consumers constraints.
 * If you want to reduce your message size, uses [UuidBytesConverter] instead.
 */
public class UuidStringConverter : NullableStringConverter<Uuid> {
    override fun encode(value: Uuid): String? = value.toString()
    override fun decode(data: String?): Uuid? = data?.let { Uuid.parse(it) }
}