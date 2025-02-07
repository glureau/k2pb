package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage
//import com.glureau.sample.MigrationAddFieldAfterNullableSerializer

@ProtoMessage
data class MigrationData(val a: String)

// Add a field
@ProtoMessage
data class MigrationAddFieldBefore(val a: String, val b: String)

@ProtoMessage
data class MigrationAddFieldAfterNullable(
    val a: String,
    val b: String,
    val c: Int,
    val d: String,
    val e: MigrationData? = null
)
/*
object MigrationAddFieldAfterNullableSC : MigrationAddFieldAfterNullableSerializer.Constructor {

}
*/
@ProtoMessage
data class MigrationAddFieldAfter(
    val a: String,
    val b: String,
    val c: Int,
    val d: String,
    val e: MigrationData
)

// Remove a field
@ProtoMessage
data class MigrationRemoveFieldBefore(val a: String, val b: String, val c: Int)

@ProtoMessage
data class MigrationRemoveFieldAfter(val a: String, val c: Int) // b has been removed

// Rename a field
@ProtoMessage
data class MigrationRenameFieldBefore(val a: String, val b: String, val c: Int)

@ProtoMessage
data class MigrationRenameFieldAfter(val a: String, val renamedB: String, val c: Int)

// Rename class
@ProtoMessage
data class MigrationRenameClassBefore(val a: String)

@ProtoMessage
data class MigrationRenameClassAfter(val a: String)

// Rename sub-class
@ProtoMessage
data class MigrationRenameSubClassBefore(val sub: MigrationRenameClassBefore)

@ProtoMessage
data class MigrationRenameSubClassAfter(val sub: MigrationRenameClassAfter)

// Migrate a sub-structure data from a place to another
@ProtoMessage
data class MigrationMoveDataBefore(val data: MigrationData)

@ProtoMessage
data class MigrationNewDataHolder(val data: MigrationData)

@ProtoMessage
data class MigrationMoveDataAfter(val dataHolder: MigrationNewDataHolder)
