package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.DelegateProtoSerializer
import com.glureau.k2pb.ProtoSerializer
import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.ProtobufWriter
import com.glureau.k2pb.compiler.mapping.InlinedTypeRecorder
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName

fun FileSpec.Builder.addInlineNode(className: ClassName, inlineNode: InlinedTypeRecorder.InlineNode) {
    TODO()
    addFileComment("Generated from $className")
    val serializerClassName = ClassName(className.packageName, "${className.simpleName}Serializer")
    addType(
        TypeSpec
            .classBuilder(serializerClassName.simpleName)
            .addSuperinterface(ProtoSerializer::class.asClassName().parameterizedBy(className))
            .addFunction(
                FunSpec.builder("encode")
                    .receiver(ProtobufWriter::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("instance", className.copy(nullable = true))
                    .addParameter("protoSerializer", DelegateProtoSerializer::class.asClassName())
                    .apply {
                        if (inlineNode.inlinedFieldType is ScalarFieldType) {
                            val accessName = "instance." + inlineNode.inlineName
                            beginControlFlow("if (instance != null)")
                            addCode(inlineNode.inlinedFieldType.writeMethodNoTag(accessName))
                            addStatement("")
                            endControlFlow()
                            return@apply
                        } else {
                            // error("Not supported yet")
                            addStatement("BOUM")
                        }
                    }
                    .build()
            )

            .addFunction(
                FunSpec.builder("decode")
                    .receiver(ProtobufReader::class.asClassName())
                    .addModifiers(KModifier.OVERRIDE)
                    .addParameter("protoSerializer", DelegateProtoSerializer::class.asClassName())
                    .returns(className.copy(nullable = true))
                    .apply {
                        if (inlineNode.inlinedFieldType is ScalarFieldType) {
                            // return ValueClassFromLib(readStringNoTag())
                            inlineNode.inlinedFieldType.readMethodNoTag()
                            addStatement("return %T(%L)", className, inlineNode.inlinedFieldType.readMethodNoTag())
                            return@apply
                        } else {
                            //error("Not supported yet")
                            addStatement("BOUM $inlineNode")
                        }
                    }
                    .build()
            )
            .build()
    )
}