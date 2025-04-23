package com.glureau.sample

import com.glureau.k2pb.ProtoPolymorphism
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.sample.PolymorphicMigration.Five
import com.glureau.sample.PolymorphicMigration.One
import com.glureau.sample.PolymorphicMigration.Two

@ProtoPolymorphism(
    parent = PolymorphicMigration::class,
    name = "PolymorphicMigration",
    deprecateOneOf = [
        ProtoPolymorphism.Deprecated(
            protoName = "Three",
            protoNumber = 3,
            deprecationReason = "This has been removed in 2.1.0 with the blipbloup feature",
            publishedInProto = false
        ),
        ProtoPolymorphism.Deprecated(
            protoName = "PolymorphicMigration.Six",
            protoNumber = 6,
            deprecationReason = "Will be removed soon",
            publishedInProto = true
        ),
    ],
    oneOf = [
        ProtoPolymorphism.Pair(One::class, 1),
        ProtoPolymorphism.Pair(Two::class, 2),
        ProtoPolymorphism.Pair(Five::class, 5),
    ]
)
interface PolymorphicMigration {
    @ProtoMessage
    data object One : PolymorphicMigration

    @ProtoMessage
    data class Two(val a: String) : PolymorphicMigration

    // Kept for testing retrocompat, all files and usage removed in reality
    @ProtoMessage("PolymorphicMigration.Three")
    data class DeprecatedThree(val a: Int) : PolymorphicMigration

    @ProtoMessage
    data class Five(val b: Long) : PolymorphicMigration

    // Kept for testing retrocompat, removed from KMP code but still preserved in proto files
    @ProtoMessage("PolymorphicMigration.Six")
    data class DeprecatedSix(val b: Long) : PolymorphicMigration
}
