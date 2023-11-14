package org.d3if3038.answerme.ui.feeds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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
    private val isNewPost = MutableLiveData(false)

    private val COLLECTION_NAME = "posts"

    fun getPosts(): LiveData<List<Post>> = posts
    fun getFethcStatus(): LiveData<FetchStatus> = fetchStatus
    fun getMessages(): LiveData<String> = message
    fun getnewPostNotif(): LiveData<Boolean> = isNewPost

    init {
        connectRealtimeDb()
        getFeeds()
    }

    private fun connectRealtimeDb() {
        firebaseDb.collection(COLLECTION_NAME)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    message.postValue("There Something Wrong...")
                    return@addSnapshotListener
                }

                if (value == null || value.isEmpty) return@addSnapshotListener

                isNewPost.value = true
            }
    }

    fun getFeeds(category: List<String>? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fetchStatus.postValue(FetchStatus.LOADING)

                val docPost = if (category.isNullOrEmpty()) {
                    fetchQuestion(firebaseDb.collection(COLLECTION_NAME))
                } else {
                    fetchQuestion(
                        firebaseDb.collection(COLLECTION_NAME),
                        category
                    )
                }

                docPost
                    .addOnSuccessListener {
                        val postBuffer = mutableListOf<Post>()

                        it.forEach { doc ->
                            postBuffer.add(doc.toObject(Post::class.java))
                        }

                        isNewPost.postValue(false)
                        fetchStatus.postValue(FetchStatus.SUCCESS)

                        posts.value = postBuffer
                    }
                    .addOnFailureListener {
                        message.value = it.message!!.split(".")[0]
                        fetchStatus.postValue(FetchStatus.FAILED)
                    }

            }
        }
    }

    private fun fetchQuestion(firebaseCollection: CollectionReference) = firebaseCollection
        .orderBy("timeStamp", Query.Direction.DESCENDING)
        .get()

    private fun fetchQuestion(
        firebaseCollection: CollectionReference,
        category: List<String>
    ) : Task<QuerySnapshot> {

        return firebaseCollection
            .whereArrayContainsAny("genres", category)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()

    }
}