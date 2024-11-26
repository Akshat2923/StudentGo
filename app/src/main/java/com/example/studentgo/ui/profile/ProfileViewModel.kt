package com.example.studentgo.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentgo.model.KnownLocationRepository
import com.example.studentgo.model.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

/* Note view models hold the business logic for fragments to use like signout etc */
class ProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _user: MutableLiveData<FirebaseUser?> = MutableLiveData(auth.currentUser)
    val user: LiveData<FirebaseUser?> = _user

    private lateinit var userRepository: UserRepository

    fun signOut() {
        auth.signOut()
        _user.value = null // Update user state
    }

    fun deleteUser(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            userRepository.deleteUserFromFirebaseCollection(auth.currentUser?.email)
        }

        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun updateUser(newEmail: String, onComplete: (Boolean) -> Unit) {
        auth.currentUser?.verifyBeforeUpdateEmail(newEmail)?.addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun setUserRepository(userRepository: UserRepository) {
        this.userRepository = userRepository
    }
}