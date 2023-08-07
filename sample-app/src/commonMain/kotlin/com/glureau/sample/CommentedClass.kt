package com.glureau.sample

import kotlinx.serialization.Serializable

/**
 * This class has kdoc comment.
 */
@Serializable
data class CommentedClass(
    /**
     * This field has kdoc comment.
     */
    // developer documentation is not exported
    /* developer documentation is not exported */
    val fieldWithComment: String = ""
)
