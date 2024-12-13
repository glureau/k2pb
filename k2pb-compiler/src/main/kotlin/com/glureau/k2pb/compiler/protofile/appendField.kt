package com.glureau.k2pb.compiler.protofile

import com.glureau.k2pb.compiler.struct.FieldInterface
import com.glureau.k2pb.compiler.struct.NumberManager
import com.glureau.k2pb.compiler.struct.OneOfField
import com.glureau.k2pb.compiler.struct.TypedField

fun StringBuilder.appendField(indentLevel: Int, field: FieldInterface, numberManager: NumberManager) {
    when (field) {
        is TypedField -> appendTypedField(indentLevel, field)
        is OneOfField -> appendOneOfField(indentLevel, field, numberManager)
    }
}