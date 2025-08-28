package com.glureau.k2pb.compiler.struct

import com.glureau.k2pb.compiler.Logger

fun sortFields(
    location: String,
    activeFields: List<FieldInterface>,
    deprecatedFields: List<IDeprecatedField>,
    onActiveField: (FieldInterface) -> Unit,
    onDeprecatedField: (IDeprecatedField) -> Unit,
    onActiveNullabilityField: (nullabilityField: NullabilitySubField, targetField: TypedField) -> Unit,
    onUnusedProtoNumber: (Int) -> Unit,
) {
    val maxProtoNumber = listOf(
        deprecatedFields.maxOfOrNull { it.protoNumber } ?: 1,
        activeFields.maxOfOrNull { it.protoNumber } ?: 1,
        activeFields.filterIsInstance<TypedField>()
            .mapNotNull { it.nullabilitySubField }
            .maxOfOrNull { it.protoNumber } ?: 1,
    ).max()

    for (index in 1..maxProtoNumber) {
        val activeField = activeFields.firstOrNull { it.protoNumber == index }
        val activeNullabilityField = activeFields.filterIsInstance<TypedField>()
            .mapNotNull { it.nullabilitySubField }
            .firstOrNull { it.protoNumber == index }
        val deprecatedField = deprecatedFields.firstOrNull { it.protoNumber == index }
        val nullsCount = listOfNotNull(activeField, activeNullabilityField, deprecatedField).count()
        val nullabilityTarget = activeNullabilityField?.let {
            activeFields.filterIsInstance<TypedField>()
                .firstOrNull { it.nullabilitySubField == activeNullabilityField }
        }
        when {
            nullsCount > 1 ->
                Logger.error("Conflict, the protoNumber $index is used by both active and deprecated fields in $location")

            nullsCount == 0 -> onUnusedProtoNumber(index)
            activeField != null -> onActiveField(activeField)
            deprecatedField != null -> onDeprecatedField(deprecatedField)
            activeNullabilityField != null && nullabilityTarget == null ->
                Logger.error("Detected a nullability field without target for protoNumber $index in $location")

            activeNullabilityField != null ->
                onActiveNullabilityField(activeNullabilityField, nullabilityTarget!!)
        }
    }
}