package com.example.mc_project.network

data class VehicleData(
    val distace_frome_me:Double,
    val id: String,
    val tripId: String,
    val startTime: String,
    val startDate: String,
    val scheduleRelationship: String,
    val routeId: String,
    val latitude: Float,
    val longitude: Float,
    val speed: Float,
    val timestamp: Long,
    val vehicleId: String,
    val vehicleLabel: String
)