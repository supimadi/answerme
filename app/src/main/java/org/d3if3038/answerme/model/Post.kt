package org.d3if3038.answerme.model

data class Post(
    var documentId: String,
    val username: String,
    val title: String,
    val question: String,
    val comments: List<Comment>? = null,
)
