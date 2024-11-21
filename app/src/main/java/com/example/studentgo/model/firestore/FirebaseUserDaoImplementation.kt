package com.example.studentgo.model.firestore

import android.util.Log
import com.example.studentgo.model.room.RoomUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseUserDaoImplementation: FirebaseUserDao {
    private val firestore = Firebase.firestore

    override suspend fun insertUser(user: FirebaseUser) {
        val x = hashMapOf(
            "email" to user.email,
            "name" to user.name,
            "score" to user.score
        )

        firestore.collection("users").document(user.email).set(x)
    }

    override suspend fun deleteUser(user: FirebaseUser) {
        firestore.collection("users").document(user.email).delete()
    }

    override suspend fun updateUser(user: FirebaseUser) {
        val x = hashMapOf(
            "email" to user.email,
            "name" to user.name,
            "score" to user.score
        )

        firestore.collection("users").document(user.email).set(x, SetOptions.merge())
    }

    override suspend fun convertToLocalModel(user: FirebaseUser): RoomUser {
        val roomUser = RoomUser(
            user.email,
            user.score,
            user.name
        )

        return roomUser
    }

    override suspend fun convertToRemoteModel(user: RoomUser): FirebaseUser {
        val firebaseUser = FirebaseUser(
            user.email,
            user.name,
            user.score
        )

        return firebaseUser
    }

    override suspend fun getUser(email: String): FirebaseUser? {
        var user: FirebaseUser? = null

        val data = firestore.collection("users").document(email).get()
            .addOnSuccessListener { document ->
                user = if (document == null) {
                    null
                } else {
                    FirebaseUser(
                        email = document.getString("email") ?: "",
                        name = document.getString("name") ?: "",
                        score = document.getLong("score")?.toInt() ?: 0
                    )
                }
            }

        return user
    }
}