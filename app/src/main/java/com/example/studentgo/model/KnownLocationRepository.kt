package com.example.studentgo.model

import androidx.annotation.Keep
import com.example.studentgo.StudentGoApp
import com.example.studentgo.model.firestore.FirebaseKnownLocationDao
import com.example.studentgo.model.room.RoomKnownLocation
import com.example.studentgo.model.room.RoomKnownLocationDao
import android.content.Context

@Keep
class KnownLocationRepository (
    context: Context,
    private val localDao: RoomKnownLocationDao = (context.applicationContext as StudentGoApp).database.roomKnownLocationDao(),
    private val remoteDao: FirebaseKnownLocationDao){

    suspend fun getKnownLocation(name: String): RoomKnownLocation? {
        var location = localDao.getLocation(name)

        if (location == null) {
            val remoteLocation = remoteDao.getLocation(name)
            location = remoteDao.convertToLocalModel(remoteLocation)
            if (location != null) {
                localDao.insertLocation(location)
            }
        }

        return location
    }

    suspend fun getAllKnownLocations(): List<RoomKnownLocation> {
        val allKnownLocations: List<RoomKnownLocation>
        val remoteAllKnownLocations = remoteDao.getAllKnownLocations()
        allKnownLocations = remoteDao.convertToLocalListOfLocations(remoteAllKnownLocations)
        if (allKnownLocations.isNotEmpty()) {
            localDao.insertAllKnownLocations(allKnownLocations)
        }

        return allKnownLocations
    }
}