package com.example.studentgo.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/* Note view models hold the business logic for fragments to use like signout etc */
class ProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _user: MutableLiveData<FirebaseUser?> = MutableLiveData(auth.currentUser)
    val user: LiveData<FirebaseUser?> = _user

    fun signOut() {
        auth.signOut()
        _user.value = null // Update user state
    }

    fun deleteUser(onComplete: (Boolean) -> Unit) {
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }
}