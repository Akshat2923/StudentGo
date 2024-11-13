package com.example.studentgo.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.studentgo.model.room.RoomKnownLocation
import com.example.studentgo.model.room.RoomKnownLocationDao
import com.example.studentgo.model.room.RoomUser
import com.example.studentgo.model.room.RoomUserDao

@Database(entities = [RoomUser::class, RoomKnownLocation::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun roomUserDao(): RoomUserDao
    abstract fun roomKnownLocationDao(): RoomKnownLocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Returns the database if it exists, otherwise executes the block
            return INSTANCE ?: synchronized(this) {
                // Builds the database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studentgo_database"
                ).build()
                // Sets the companion object instance value
                INSTANCE = instance
                // Returns the new instance
                instance
            }
        }
    }
}