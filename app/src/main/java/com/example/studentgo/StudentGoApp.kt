package com.example.studentgo

import android.app.Application
import com.example.studentgo.model.AppDatabase

class StudentGoApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this)}
}