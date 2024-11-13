package com.example.studentgo.model

import android.content.Context
import androidx.annotation.Keep
import com.example.studentgo.StudentGoApp
import com.example.studentgo.model.firestore.FirebaseUserDao
import com.example.studentgo.model.room.RoomUser
import com.example.studentgo.model.room.RoomUserDao

@Keep
class UserRepository (
    context: Context,
    private val localDao: RoomUserDao = (context.applicationContext as StudentGoApp).database.roomUserDao(),
    private val remoteDao: FirebaseUserDao
) {
    // Implement methods here which attempt to get information from the local database
        // before fetching from Firestore. These methods will be interacted with from another
        // layer of the architecture

    suspend fun getUser(email: String): RoomUser? {
        var user = localDao.getUser(email)

        if (user == null) {
            val remoteUser = remoteDao.getUser(email)
            user = remoteDao.convertToLocalModel(remoteUser)
            if (user != null) {
                localDao.insertUser(user)
            }
        }

        return user
    }

    suspend fun updateUser(user: RoomUser) {
        localDao.insertUser(user)
        remoteDao.insertUser(remoteDao.convertToRemoteModel(user))
    }
}