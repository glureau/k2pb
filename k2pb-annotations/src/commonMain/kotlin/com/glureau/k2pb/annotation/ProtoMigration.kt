package com.glureau.k2pb.annotation

@Target(AnnotationTarget.PROPERTY)
public annotation class ProtoMigration(val unspecified: UnspecifiedBehavior)

/**
 * Protobuf defines a default value for scalar and enum types, for example 0 for Int, false for Boolean, etc.
 * When a scalar/enum is serialized, if it's the default value, it's actually not encoded (saving some bytes).
 *
 * Problem comes with migration, because we may have 2 scenarios:
 * - a non-nullable value is transformed to become a nullable value
 * - a new nullable value is added
 *
 * In the first scenario, if we tried to serialize a default value before the code evolution,
 * the resulting ByteArray would have no trace of it (default value are not encoded).
 *
 * In the second scenario, because it wasn't there before, there was also nothing to serialize.
 *
 * Both scenarios are resulting to the same ByteArray encoded with the previous version of the code.
 * But post-migration, what do we expect?
 * - if it was a non-nullable value, we expect the default value, it's the only possible solution
 *      because it was not serialized
 * - if it was not there before, we expect a null, because we know it wasn't there before.
 *
 * So because in both cases, the previously encoded ByteArray is strictly equivalent, but as a developer we have
 * different expectations, based on a code history which is not available, we need to specify the behavior explicitly.
 */

// ajout de champ non nullable, on s'attend à la valeur par défaut
// ajout de champ nullable, on s'attend à null
public enum class UnspecifiedBehavior {
    /**
     * To be used when adding a new nullable field.
     */
    NULL,

    /**
     * To be used when transforming a non-nullable field to a nullable field.
     *
     * From a pure protobuf perspective, it's the expected behavior when adding a scalar field, as there's no null.
     * As such, it's also the default behavior of KMP is the migration annotation is not used.
     */
    DEFAULT,
}