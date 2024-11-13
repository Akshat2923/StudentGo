package com.example.studentgo.model.firestore

import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.studentgo.model.room.RoomKnownLocation

interface FirebaseKnownLocationDao {
    suspend fun getAllKnownLocations(): List<FirebaseKnownLocation>

    suspend fun getLocation(name: String): FirebaseKnownLocation

    suspend fun convertToLocalModel(knownLocation: FirebaseKnownLocation): RoomKnownLocation?

    suspend fun convertToLocalListOfLocations(locations: List<FirebaseKnownLocation>): List<RoomKnownLocation> {
        return locations.map { location ->
            RoomKnownLocation(
                location.name,
                location.value,
                location.latitude,
                location.longitude
            )
        }
    }
}