package org.d3if3038.answerme.ui.feeds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.d3if3038.answerme.model.FetchStatus
import org.d3if3038.answerme.model.Post

class FeedsViewModel : ViewModel() {
    private val firebaseDb = Firebase.firestore

    private val posts = MutableLiveData<List<Post>>()
    private val fetchStatus = MutableLiveData<FetchStatus>()
    private val message = MutableLiveData<String>()

    fun getPosts(): LiveData<List<Post>> = posts
    fun getFethcStatus(): LiveData<FetchStatus> = fetchStatus
    fun getMessages(): LiveData<String> = message

    fun fetchMyPost() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fetchStatus.postValue(FetchStatus.LOADING)

                firebaseDb.collection("posts")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val postBuffer = mutableListOf<Post>()

                        @Suppress("UNCHECKED_CAST")
                        querySnapshot.forEach {
                            postBuffer.add(
                                Post(
                                    username = it.getString("username")!!,
                                    title = it.getString("title")!!,
                                    question = it.getString("question")!!,
                                    genres = it.get("genres") as List<String>,
                                    avatar = it.getString("avatar")!!
                                )
                            )
                        }

                        posts.value = postBuffer
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