package com.glureau.k2pb

/**
 * Ensures backward compatibility, when adding a new nullable field, we want to be able to distinguish
 * between the absence of the field (old format read) and the default value (ex "" for String).
 */
public enum class ExplicitNullability {
    /**
     * A  nullable field has not been explicitly defined, probably an evolution from a previous format.
     * In this case, the associated field will be returned without protobuf default value.
     * Example, a new nullable enum has been added, the default protobuf value would have been
     * technically the first enum entry
     * K2PB will return the associated value without any requirement.
     */
    UNKNOWN,

    /**
     * A nullable field has been explicitly set to NULL.
     * K2PB ignores the associated field, effectively returning a null for the value.
     */
    NULL,

    /**
     * A nullable field has been explicitly set to a non-null value.
     * The associated value could be the protobuf default ("" != null).
     * During deserialization, K2PB requires that the associated value is not null, or else throws.
     */
    NOT_NULL;
    public companion object {
        public const val PROTO_TYPE: String = "ExplicitNullability"
    }
}
