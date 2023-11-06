package org.d3if3038.answerme.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.d3if3038.answerme.model.Comment
import org.d3if3038.answerme.model.FetchStatus
import org.d3if3038.answerme.model.Post

class CommentViewModel : ViewModel() {
    private val post =  MutableLiveData<Post>()
    private val comments = MutableLiveData<MutableList<Comment>>()
    private val message = MutableLiveData<String>()
    private val fetchStatus = MutableLiveData<FetchStatus>()
    private val commentPostStatus = MutableLiveData<FetchStatus>()

    private val firebaseDb = Firebase.firestore

    fun getPost() : LiveData<Post> = post
    fun getMessage() : LiveData<String> = message
    fun getFetchStatus() : LiveData<FetchStatus> = fetchStatus
    fun getComments(): LiveData<MutableList<Comment>> = comments
    fun getCommentPostStatus() : LiveData<FetchStatus> = commentPostStatus

    fun postComment(comment: Comment, docId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val commentsBuffer = mutableListOf<Comment>()
                commentPostStatus.postValue(FetchStatus.LOADING)

                if (!comments.value.isNullOrEmpty()) {
                    commentsBuffer.addAll(comments.value!!)
                }

                commentsBuffer.add(comment)

                firebaseDb.collection("posts").document(docId)
                    .update("comments", commentsBuffer)
                    .addOnSuccessListener {
                        comments.value?.add(comment)
                        commentPostStatus.postValue(FetchStatus.SUCCESS)
                    }
                    .addOnFailureListener {
                        message.postValue("Failed to post comment...")
                        commentPostStatus.postValue(FetchStatus.FAILED)
                    }

            }
        }
    }

    fun fetchQuestion(docId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fetchStatus.postValue(FetchStatus.LOADING)

                firebaseDb.collection("posts").document(docId)
                    .get()
                    .addOnSuccessListener { doc ->
                        val commentsBuffer = mutableListOf<Comment>()

                        val retrivedPost = doc.toObject(Post::class.java) ?: return@addOnSuccessListener

                        post.value = retrivedPost

                        if (!retrivedPost.comments.isNullOrEmpty())
                            commentsBuffer.addAll(post.value!!.comments!!)

                        comments.value = commentsBuffer
                        fetchStatus.postValue(FetchStatus.SUCCESS)
                    }
                    .addOnFailureListener {
                        message.value = it.message!!.split(".")[0]
                        fetchStatus.postValue(FetchStatus.FAILED)
                    }
            }
        }


    }
}