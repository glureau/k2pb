package com.glureau.k2pb.compiler

import com.glureau.k2pb.compiler.mapping.InlinedTypeRecorder
import com.glureau.k2pb.compiler.struct.EnumNode
import com.glureau.k2pb.compiler.struct.FieldType
import com.glureau.k2pb.compiler.struct.ListType
import com.glureau.k2pb.compiler.struct.MapType
import com.glureau.k2pb.compiler.struct.MessageNode
import com.glureau.k2pb.compiler.struct.ReferenceType
import com.glureau.k2pb.compiler.struct.TypedField

class ProtobufAggregator {
    internal val messages = mutableListOf<MessageNode>()
    internal val enums = mutableListOf<EnumNode>()

    private val qualifiedNameSet = mutableSetOf<String>()

    fun recordMessageNode(it: MessageNode) {
        messages += it
        require(qualifiedNameSet.contains(it.qualifiedName).not()) { "Duplicated qualified name: ${it.qualifiedName}" }
        qualifiedNameSet += it.qualifiedName
        Logger.info("recordMessageNode ${it.qualifiedName} => ${it.name}")
        TypeResolver.qualifiedNameToProtobufName[it.qualifiedName] = it.name
    }

    fun recordEnumNode(it: EnumNode) {
        enums += it
        require(qualifiedNameSet.contains(it.qualifiedName).not()) { "Duplicated qualified name: ${it.qualifiedName}" }
        qualifiedNameSet += it.qualifiedName
        Logger.info("recordEnumNode ${it.qualifiedName} => ${it.name}")
        TypeResolver.qualifiedNameToProtobufName[it.qualifiedName] = it.name
    }

    fun unknownReferences(): Set<String> {
        val fieldTypeList: List<FieldType> = messages
            .flatMap { it.fields }
            .filter { (it as? TypedField)?.annotatedSerializer == null }
            .flatMap { it.allFieldTypes() }
        val stdRefs = fieldTypeList.filterIsInstance<ReferenceType>()
            .also { it.forEach { Logger.warn("GREG : UNKNOWN REF $it ") } }
            .map { it.name }
        // TODO: recursivity + TU for List&Map
        val listRefs = fieldTypeList.filterIsInstance<ListType>()
            .mapNotNull { if (it.repeatedType is ReferenceType) it.repeatedType.name else null }
        val mapRefs = fieldTypeList.filterIsInstance<MapType>()
            .flatMap {
                val list = mutableListOf<String>()
                if (it.keyType is ReferenceType) list.add(it.keyType.name)
                if (it.valueType is ReferenceType) list.add(it.valueType.name)
                list
            }
        val references: Set<String> = (stdRefs + listRefs + mapRefs).toSet()
        return references - (qualifiedNameSet + InlinedTypeRecorder.getAllInlinedTypes().keys)
    }
}

