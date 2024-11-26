package com.example.studentgo.model.firestore

import com.example.studentgo.model.room.RoomUser

interface FirebaseUserDao {
    suspend fun insertUser(user: FirebaseUser)

    suspend fun updateUser(user: FirebaseUser)

    suspend fun deleteUser(email: String)

    suspend fun getUser(email: String): FirebaseUser?

    suspend fun convertToLocalModel(user: FirebaseUser): RoomUser?

    suspend fun convertToRemoteModel(user: RoomUser): FirebaseUser
}