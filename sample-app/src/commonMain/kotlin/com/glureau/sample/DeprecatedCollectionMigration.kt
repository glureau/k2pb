package com.glureau.sample

import com.glureau.k2pb.annotation.DeprecatedField
import com.glureau.k2pb.annotation.ProtoMessage

@ProtoMessage
data class DeprecatedCollectionStart(
    val names: List<String>,
    val items: List<CommonClass>,
    val b: String,
)

@ProtoMessage(
    deprecatedFields = [
        DeprecatedField(
            protoName = "names",
            protoNumber = 1,
            protoType = "string",
            repeated = true,
            deprecationReason = "Field 'names' has been removed",
        ),
        DeprecatedField(
            protoName = "items",
            protoNumber = 2,
            protoType = "CommonClass",
            repeated = true,
            deprecationReason = "Field 'items' has been removed",
        ),
    ],
)
data class DeprecatedCollectionEnd(
    val b: String,
)

@ProtoMessage(
    deprecatedFields = [
        DeprecatedField(
            protoName = "names",
            protoNumber = 1,
            protoType = "string",
            repeated = true,
            deprecationReason = "Field 'names' has been removed",
            publishedInProto = false,
        ),
        DeprecatedField(
            protoName = "items",
            protoNumber = 2,
            protoType = "CommonClass",
            repeated = true,
            deprecationReason = "Field 'items' has been removed",
            publishedInProto = false,
        ),
    ],
)
data class DeprecatedCollectionReserved(
    val b: String,
)
