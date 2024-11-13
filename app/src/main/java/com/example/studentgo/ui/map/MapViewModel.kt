package com.example.studentgo.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentgo.model.KnownLocationRepository
import com.example.studentgo.model.UserRepository
import com.example.studentgo.model.room.RoomKnownLocation
import com.example.studentgo.model.room.RoomUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MapViewModel() : ViewModel() {
    private lateinit var knownLocationRepository: KnownLocationRepository
    private lateinit var userRepository: UserRepository

    fun initialize(knownLocationRepository: KnownLocationRepository, userRepository: UserRepository) {
        this.knownLocationRepository = knownLocationRepository
        this.userRepository = userRepository
    }

    private val _auth = MutableLiveData<FirebaseAuth>()
    val auth: LiveData<FirebaseAuth> = _auth

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _user = MutableLiveData<RoomUser?>()
    val user: LiveData<RoomUser?> = _user

    private val _locations = MutableLiveData<List<RoomKnownLocation>>()
    val locations: LiveData<List<RoomKnownLocation>> = _locations

    fun getKnownLocations() {
        viewModelScope.launch {
            _locations.value = knownLocationRepository.getAllKnownLocations()
        }
    }

    fun setAuth(firebaseAuth: FirebaseAuth) {
        _auth.value = firebaseAuth
    }

    fun setEmail(email: String) {
        _email.value = email
    }

    fun getUser(email: String) {
        viewModelScope.launch {
            _user.value = userRepository.getUser(email)
        }
    }

    fun updateUser(user: RoomUser) {
        viewModelScope.launch {
            userRepository.updateUser(user)
        }
    }
}