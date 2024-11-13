package com.example.studentgo.model.room

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoomKnownLocationDao {
    @Query("SELECT * FROM knownLocations")
    suspend fun getAllKnownLocations(): List<RoomKnownLocation>

    @Query("SELECT * FROM knownLocations WHERE name = :name")
    suspend fun getLocation(name: String): RoomKnownLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: RoomKnownLocation)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKnownLocations(locations: List<RoomKnownLocation>)
}