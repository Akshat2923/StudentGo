package com.example.studentgo.model.firestore

data class FirebaseKnownLocation(
    val name: String,
    val value: Int,
    val latitude: Double,
    val longitude: Double,
) {
//    TODO: implement equals, toString, and hashCode()
    constructor(): this("", 0, 0.0, 0.0)
}