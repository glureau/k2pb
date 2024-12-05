package com.glureau.k2pb.compiler.poet

import com.glureau.k2pb.ProtoIntegerType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName

val ProtoWireTypeClassName = ClassName("com.glureau.k2pb.runtime.ktx", "ProtoWireType")
// DEFAULT is actually an entry, not a class... this is a way to declare the import more nicely
val ProtoIntegerTypeDefault = ProtoIntegerType::class.asClassName().nestedClass("DEFAULT")
