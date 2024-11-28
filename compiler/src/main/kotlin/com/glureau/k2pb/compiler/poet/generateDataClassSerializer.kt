package com.glureau.k2pb.compiler.poet

import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.OneOfField
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.TypedField
import com.glureau.k2pb.compiler.struct.asClassName
import com.glureau.k2pb.compiler.struct.decodeField
import com.glureau.k2pb.compiler.struct.decodeFieldVariableDefinition
import com.glureau.k2pb.compiler.struct.encodeField
import com.glureau.k2pb.compiler.struct.nameOrDefault
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toClassName

fun FunSpec.Builder.generateDataClassSerializerEncode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
) {
    addStatement("if (instance == null) return")
    messageNode.fields.forEach {
        encodeField(it)
    }
}

fun FunSpec.Builder.generateDataClassSerializerDecode(
    messageNode: MessageNode,
    instanceName: String,
    protoSerializerName: String
) {
    messageNode.fields.forEach {
        decodeFieldVariableDefinition(it)
    }
    addStatement("")

    beginControlFlow("while (!eof)")
    beginControlFlow("when (val tag = readTag())")
    messageNode.fields.forEach { f ->
        beginControlFlow("${f.protoNumber} ->")
        decodeField(f)
        endControlFlow()
    }
    beginControlFlow("else -> ")
    //addStatement("pushBackTag()") // ???
    // TODO: Incomplete implementation, ignoring a tag should also ignore the next data...
    addStatement("// Ignore unknown tag??")
    addStatement("error(\"Ignoring unknown tag: \$tag\")")
    endControlFlow()

    endControlFlow() // when (tag)
    endControlFlow() // while (!eof)

    addStatement("return %T(", messageNode.asClassName())
    messageNode.fields.forEach {
        when (it) {
            is OneOfField -> {
                // TODO()
            }

            is TypedField -> {
                /*if (it.annotatedSerializer != null) {
                    val str = if ((it.type is ReferenceType) && it.type.isNullable == false) {
                        "  ${it.name} = requireNotNull(%T().decode(${it.nameOrDefault()})) /* A */"
                    } else {
                        "  ${it.name} = (${it.nameOrDefault()})?.let { %T().decode(it) } /* B */"
                    }
                    addStatement(str, it.annotatedSerializer.toClassName())
                } else {*/
                    val str = if ((it.type is ReferenceType) && it.type.isNullable == false) {
                        if (it.type.inlineOf?.isNullable == true) {
                            "  ${it.name} = ${it.name} ?: ${it.type.name}(null), /* C */"
                        } else {
                            "  ${it.name} = requireNotNull(${it.name}), /* D */"
                        }
                    } else {
                        "  ${it.name} = ${it.nameOrDefault()}, /* E */"
                    }
                    addStatement(str)
                //}
            }
        }
    }
    addStatement(")")
}