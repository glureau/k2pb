package com.glureau.sample.lib

import com.glureau.k2pb.annotation.ProtoMessage

@ProtoMessage
@JvmInline
value class ValueClassFromLib(val myValueIsString: String)


@ProtoMessage
@JvmInline
value class NullableValueClassFromLib(val myValueIsString: String?)
