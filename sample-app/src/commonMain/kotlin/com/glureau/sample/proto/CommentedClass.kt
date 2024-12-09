package com.glureau.sample.proto

import com.glureau.k2pb.annotation.ProtoMessage

/**
 * This class has kdoc comment.
 */
@ProtoMessage
data class CommentedClass(
    /**
     * This field has kdoc comment.
     */
    // developer documentation is not exported
    /* developer documentation is not exported */
    val fieldWithComment: String = ""
)