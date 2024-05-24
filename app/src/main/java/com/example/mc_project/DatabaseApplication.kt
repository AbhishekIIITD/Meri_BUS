package com.example.mc_project

import android.app.Application
import com.example.mc_project.Database.AppDatabase

class DatabaseApplication: Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}