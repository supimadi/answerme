package org.d3if3038.answerme.model

import java.io.Serializable

data class Comment(
    var md5: String = "",
    var commentText: String = "",
    var username: String = "",
    var avatarUrl: String = "",
    var timeStamp: Long = System.currentTimeMillis(),
    var timeElapsed: Long = System.nanoTime()
) : Serializable
