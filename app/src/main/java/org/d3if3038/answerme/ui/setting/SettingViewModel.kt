package org.d3if3038.answerme.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.d3if3038.answerme.model.Profile

class SettingViewModel: ViewModel() {
    private val firebaseDb = Firebase.firestore
    private val message = MutableLiveData<String>()
    private val profile = MutableLiveData<Profile>()

    fun getProfile(): LiveData<Profile> = profile
    fun getErrorMessage(): LiveData<String> = message

    fun saveProfile(profileInput: Profile) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dataSource = Source.SERVER
                firebaseDb.collection("profile").document(profileInput.username)

                val document = firebaseDb.collection("profile")
                    .document(profileInput.username)

                document.get(dataSource)
                    .addOnSuccessListener { ref ->
                        if (ref.exists()) {
                            message.postValue("Username Already Exists.")
                        } else {
                            document
                                .set(profileInput)
                                .addOnFailureListener {
                                    message.postValue(it.message?.split(".")?.get(0) ?: "Silahkan Coba Lagi.")
                                }
                                .addOnSuccessListener {
                                    profile.value = profileInput
                                    message.postValue("Success Setup Profile!")
                                }
                        }
                    }
                    .addOnFailureListener {
                        message.postValue(it.message?.split(".")?.get(0) ?: "Silahkan Coba Lagi.")
                    }

            }
        }
    }
}