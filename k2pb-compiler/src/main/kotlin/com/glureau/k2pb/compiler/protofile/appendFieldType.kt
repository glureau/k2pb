package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.mapping.customConverterType
import com.glureau.k2pb.compiler.struct.FieldType
import com.glureau.k2pb.compiler.struct.ListType
import com.glureau.k2pb.compiler.struct.MapType
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.ScalarFieldType
import com.glureau.k2pb.compiler.struct.SetType
import com.google.devtools.ksp.symbol.KSType

fun StringBuilder.appendFieldType(type: FieldType, annotatedConverter: KSType?) {
    if (annotatedConverter != null) {
        annotatedConverter.customConverterType()?.let { convertedType ->
            append(convertedType.protoType.name)
            return
        }
        error("Annotated custom converter not supported yet: $annotatedConverter")
    }
    when (type) {
        is ScalarFieldType -> appendScalarType(type)
        is ReferenceType -> appendReferenceType(type)
        is ListType -> appendListType(type.repeatedType, annotatedConverter)
        // Set is encoded as a List
        is SetType -> appendListType(type.repeatedType, annotatedConverter)
        is MapType -> appendMapType(type)
    }
}
