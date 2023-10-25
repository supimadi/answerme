package org.d3if3038.answerme.ui.createpost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.d3if3038.answerme.model.Post

class CreatePostViewModel : ViewModel() {
    private val firebaseDb = Firebase.firestore

    fun pushPost(post: Post) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val document = firebaseDb.collection("posts").document()
                post.documentId = document.id

                document
                    .set(post)
                    .addOnFailureListener {

                    }
            }
        }

    }
}