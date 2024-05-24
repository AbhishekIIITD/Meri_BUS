package com.example.mc_project.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import java.util.concurrent.Executors
import kotlin.concurrent.Volatile


@Database(entities = arrayOf(StopInfo::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun StopDao(): StopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}