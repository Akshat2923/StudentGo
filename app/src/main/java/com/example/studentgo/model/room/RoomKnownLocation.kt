package com.example.studentgo.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knownLocations")
data class RoomKnownLocation(
    @PrimaryKey val name: String,
    val value: Int,
    val latitude: Double,
    val longitude: Double,
) {
//    TODO: implement equals, toString, and hashCode()
}