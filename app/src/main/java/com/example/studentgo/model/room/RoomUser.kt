package com.example.studentgo.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class RoomUser(
    @PrimaryKey val email: String,
    var score: Int,
    val name: String,
) {
    //    TODO: implement equals, toString, and hashCode()
}