package org.d3if3038.answerme.ui.mypost

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

class MyPostViewModel : ViewModel() {
    private val firebaseDb = Firebase.firestore
    private val myPost = MutableLiveData<List<Post>>()
    private val message = MutableLiveData<String>()
    private val fetchStatus = MutableLiveData<FetchStatus>()

    fun getMyPost() : LiveData<List<Post>> = myPost
    fun getMessage() : LiveData<String> = message
    fun getFetchStatus() : LiveData<FetchStatus> = fetchStatus

    fun fetchMyPost(username: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fetchStatus.postValue(FetchStatus.LOADING)

                firebaseDb.collection("posts").whereEqualTo(
                    "username", username
                )
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

                    myPost.value = postBuffer
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