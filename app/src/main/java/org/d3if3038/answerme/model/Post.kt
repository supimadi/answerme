package org.d3if3038.answerme.model

data class Post(
    var documentId: String? = null,
    val username: String,
    val title: String,
    val genres: List<String>,
    val question: String,
    val comments: List<Comment>? = null,
)
