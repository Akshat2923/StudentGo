package com.example.studentgo.model.firestore

import androidx.lifecycle.MutableLiveData
import com.example.studentgo.model.room.RoomKnownLocation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseKnownLocationDaoImplementation: FirebaseKnownLocationDao {
    private val firestore = Firebase.firestore

    override suspend fun getAllKnownLocations(): List<FirebaseKnownLocation> {
        val locations = mutableListOf<FirebaseKnownLocation>()
        val snapshot = firestore.collection("knownLocations").get().await()
        for (location in snapshot.documents) {
            location.toObject(FirebaseKnownLocation::class.java)?.let { locations.add(it) }
        }
        return locations
    }

    override suspend fun getLocation(name: String): FirebaseKnownLocation {
        TODO("Not yet implemented")
    }

    override suspend fun convertToLocalModel(knownLocation: FirebaseKnownLocation): RoomKnownLocation {
        val roomKnownLocation: RoomKnownLocation = RoomKnownLocation(
            knownLocation.name,
            knownLocation.value,
            knownLocation.latitude,
            knownLocation.longitude
        )

        return roomKnownLocation
    }

}