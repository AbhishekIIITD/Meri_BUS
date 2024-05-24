package com.example.mc_project.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mc_project.Database.StopDao
import com.example.mc_project.Database.StopInfo
import com.example.mc_project.DatabaseApplication
import kotlinx.coroutines.flow.Flow


class StopsDbViewModel<LatLng>(private val stopDao: StopDao): ViewModel() {
    fun saveLocation(value: LatLng) {

    }

}