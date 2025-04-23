package com.glureau.sample

import com.glureau.k2pb.DelegateProtoCodec
import com.glureau.k2pb.ProtoDecoder
import com.glureau.k2pb.ProtobufReader
import com.glureau.k2pb.annotation.ProtoMessage
import com.glureau.k2pb.annotation.ProtoPolymorphism
import com.glureau.sample.PolymorphicMigration.*

@ProtoPolymorphism(
    parent = PolymorphicMigration::class,
    name = "PolymorphicMigration",
    deprecateOneOf = [
        ProtoPolymorphism.Deprecated(
            protoName = "Three",
            protoNumber = 3,
            deprecationReason = "This has been removed in 2.1.0 with the blipbloup feature",
            publishedInProto = false,
        ),
        ProtoPolymorphism.Deprecated(
            protoName = "PolymorphicMigration.Six",
            protoNumber = 6,
            deprecationReason = "Will be removed soon, should be migrated to Seven",
            publishedInProto = true,
            migrationDecoder = SixToSevenMigrationDecoder::class
        ),
    ],
    oneOf = [
        ProtoPolymorphism.Child(One::class, 1),
        ProtoPolymorphism.Child(Two::class, 2),
        ProtoPolymorphism.Child(Five::class, 5),
        ProtoPolymorphism.Child(Seven::class, 7),
    ]
)
interface PolymorphicMigration {
    @ProtoMessage
    data object One : PolymorphicMigration

    @ProtoMessage
    data class Two(val a: String) : PolymorphicMigration

    // Kept for testing protobuf retrocompat (proto file required), all files and usage removed in reality
    @ProtoMessage("PolymorphicMigration.Three")
    data class DeprecatedThree(val a: Int) : PolymorphicMigration

    @ProtoMessage
    data class Five(val b: Long) : PolymorphicMigration

    // Kept for testing protoc retrocompat, removed from KMP code but still preserved in proto files
    @ProtoMessage("PolymorphicMigration.Six")
    data class DeprecatedSix(val b: Long) : PolymorphicMigration

    @ProtoMessage // Migrate the 6 by a custom encoder
    data class Seven(val b: String) : PolymorphicMigration

    class SixToSevenMigrationDecoder : ProtoDecoder<Seven> {
        override fun ProtobufReader.decode(protoCodec: DelegateProtoCodec): Seven? {
            var b: Long? = null
            while (!eof) {
                when (readTag()) {
                    1 -> {
                        b = readLong(com.glureau.k2pb.ProtoIntegerType.DEFAULT)
                    }
                }
            }
            return Seven(
                b = (b ?: 0).toString(),
            )
        }
    }
}
