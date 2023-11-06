package org.d3if3038.answerme.model

import androidx.annotation.Keep
import java.io.Serializable


data class Post(
    var documentId: String? = null,
    var username: String = "",
    var avatar: String = "",
    var title: String = "",
    var genres: List<String> = mutableListOf(""),
    var question: String = "",
    var comments: List<Comment>? = null,
    var deleted: Boolean = false,
    var timeStamp: Long = System.currentTimeMillis(),
    var timeElapsed: Long = System.nanoTime()
) : Serializable
