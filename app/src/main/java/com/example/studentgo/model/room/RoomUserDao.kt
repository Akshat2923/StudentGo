package com.example.studentgo.model.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoomUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(roomUser: RoomUser)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUser(email: String): RoomUser?

    @Delete
    suspend fun deleteUser(roomUser: RoomUser)
    // TODO: the above should run whenever a user deletes their account, which is already happening on firebase
}