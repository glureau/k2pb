package com.glureau.sample.lib

import com.glureau.k2pb.annotation.ProtoMessage
import kotlin.jvm.JvmInline

@ProtoMessage
@JvmInline
value class ValueClassFromLib(val myValueIsString: String)


@ProtoMessage
@JvmInline
value class NullableValueClassFromLib(val myValueIsString: String?)
