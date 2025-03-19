package com.glureau.sample

import com.glureau.k2pb.annotation.ProtoMessage

@ProtoMessage
data class MigrationData(val a: String)

// Add a field
@ProtoMessage
data class MigrationAddFieldBefore(val a: String, val b: String)

@ProtoMessage(constructor = Any::class)
data class MigrationAddFieldAfterNullable(
    val a: String,
    val b: String,
    val c: Int,
    val d: String?,
    val e: MigrationData?
)

@ProtoMessage(constructor = MigrationAddFieldAfterSC::class)
data class MigrationAddFieldAfter(
    val a: String,
    val b: String,
    val c: Int,
    val d: String,
    val e: MigrationData
)
// Simple example, no need of external information so it can be a singleton
// As it doesn't need anything to be constructed, no other declaration is required
object MigrationAddFieldAfterSC : MigrationAddFieldAfterSerializer.Builder {
    override fun invoke(
        a: String?,
        b: String?,
        c: Int?,
        d: String?,
        e: MigrationData?
    ): MigrationAddFieldAfter? = MigrationAddFieldAfter(
        a = requireNotNull(a), // Throwing will cancel the deserialization call entirely
        b = b ?: "", // Providing a default value avoid full cancellation, but better to follow Protobuf scalar defaults
        // Here we allow to not follow protobuf defaults, exposing a different interpretation if protoc classes are used
        // Quite often it may be required, and the code using protoc classes may have to re-implement those edge cases.
        c = c ?: 33,
        d = d ?: "hardcoded in migration",
        e = MigrationData("hardcoded here too"),
    )
}

// Work in progress HERE
@ProtoMessage//(constructor = MigrationAddFieldAfterDynamicBuilder::class)
data class MigrationAddFieldAfterDynamic(
    val a: String,
    val b: String,
    val c: Int,
    val d: String,
    val e: MigrationData
)
// More complex usage, requiring external information to be constructed.
// Here a creation of this instance needs to be done and declared explicitly.
class MigrationAddFieldAfterDynamicBuilder(private val aRuntimeValue: String) :
    MigrationAddFieldAfterSerializer.Builder {
    override fun invoke(
        a: String?,
        b: String?,
        c: Int?,
        d: String?,
        e: MigrationData?
    ): MigrationAddFieldAfter? = MigrationAddFieldAfter(
        a = requireNotNull(a), // Throwing will cancel the deserialization call entirely
        b = b ?: "", // Providing a default value avoid full cancellation, but better to follow Protobuf scalar defaults
        // Here we allow to not follow protobuf defaults, exposing a different interpretation if protoc classes are used
        // Quite often it may be required, and the code using protoc classes may have to re-implement those edge cases.
        c = c ?: 33,
        d = d ?: aRuntimeValue,
        e = MigrationData(aRuntimeValue),
    )
}


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
