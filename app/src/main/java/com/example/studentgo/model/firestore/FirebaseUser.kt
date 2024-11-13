package com.example.studentgo.model.firestore

data class FirebaseUser(
    val email: String,
    val name: String,
    val score: Int
) {
    //    TODO: implement equals, toString, and hashCode()
}