package org.d3if3038.answerme.ui.createquestion

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

class CreateQuestionViewModel : ViewModel() {
    private val message = MutableLiveData<String>()
    private val postStatus = MutableLiveData<FetchStatus>()
    private val firebaseDb = Firebase.firestore

    fun getMessage(): LiveData<String> = message
    fun getStatus(): LiveData<FetchStatus> = postStatus

    fun pushPost(post: Post) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val document = firebaseDb.collection("posts").document()
                var isSuccess = false
                post.documentId = document.id

                document
                    .set(post)
                    .addOnFailureListener {
                        message.postValue(it.message!!.split(".")[0])
                        postStatus.postValue(FetchStatus.FAILED)
                    }
                    .addOnSuccessListener {
                        message.postValue("Success Post a New Question!")
                        postStatus.postValue(FetchStatus.SUCCESS)
                        isSuccess = true
                    }
            }
        }
    }
}