package com.example.studentgo.model.firestore

import com.example.studentgo.model.room.RoomUser

interface FirebaseUserDao {
    suspend fun insertUser(user: FirebaseUser)

    suspend fun updateUser(user: FirebaseUser)

    suspend fun deleteUser(user: FirebaseUser)
    // TODO: the above should run whenever a user deletes their account, which is already happening on firebase

    suspend fun getUser(email: String): FirebaseUser?

    suspend fun convertToLocalModel(user: FirebaseUser): RoomUser?

    suspend fun convertToRemoteModel(user: RoomUser): FirebaseUser
}