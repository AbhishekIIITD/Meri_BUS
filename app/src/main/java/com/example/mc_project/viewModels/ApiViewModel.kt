package com.example.mc_project.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mc_project.StopsDetails
import com.example.mc_project.network.OneDelhiApi
import com.example.mc_project.network.VehicleData
import com.example.mc_project.network.VehiclePositionsApi
import com.google.transit.realtime.GtfsRealtime
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.ByteArrayInputStream
import java.io.IOException
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

sealed interface StopsUiState {
    data class Success(val stopDetails: List<StopsDetails>?) : StopsUiState
    object Error : StopsUiState
    object Loading : StopsUiState
}

sealed interface buesyouarein {
    data class Success(val buesyouarein: List<VehicleData>?) : buesyouarein
    object Error : buesyouarein
    object Loading : buesyouarein
}

class ApiViewModel : ViewModel() {

    var bus: buesyouarein by mutableStateOf(buesyouarein.Loading)
    var stopsUiState: StopsUiState by mutableStateOf(StopsUiState.Loading)

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        stopsUiState = StopsUiState.Loading
        bus=buesyouarein.Loading
    }

    suspend fun fetchVehiclePositionsData(lat: Double, long: Double) {
        val apiKey = "gNra2ydAOewpNFchZIzAlYhvxUwmuzAW"
        val apiService = VehiclePositionsApi.retrofitService

        try {
            val response = apiService.getVehiclePositionsData(apiKey)
            if (response.isSuccessful) {
                val protoData = response.body()?.bytes()
                if (protoData != null) {
                    // Parse the GTFS Realtime data using GTFS Realtime Bindings
                    val feedMessage =
                        GtfsRealtime.FeedMessage.parseFrom(ByteArrayInputStream(protoData))
                    val data =( parseFeedMessage(feedMessage, lat, long, 0.5))
                    println("----------------->"+data)
                   bus= buesyouarein.Success(data)
                    // Process the parsed data as needed
                } else {
                    println("Received null data")
                    buesyouarein.Error
                }
            } else {
                println("API error: ${response.code()} ${response.message()}")
                bus=  buesyouarein.Error
            }
        } catch (e: Exception) {
            println("Exception: ${e.message}")
            bus=  buesyouarein.Error
        }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Float, lon2: Float): Double {
        val theta = lon1 - lon2
        var dist = sin(Math.toRadians(lat1)) * sin(Math.toRadians(lat2.toDouble())) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2.toDouble())) *
                cos(Math.toRadians(theta))
        dist = acos(dist)
        dist = Math.toDegrees(dist)
        dist *= 60 * 1.1515
        return dist
    }

    fun parseFeedMessage(
        feedMessage: GtfsRealtime.FeedMessage,
        lat: Double,
        long: Double,
        maxDistance: Double
    ): List<VehicleData> {
        val vehicleDataList = mutableListOf<VehicleData>()

        feedMessage.entityList.forEach { entity ->
            entity.vehicle?.let { vehicle ->
                val distance = calculateDistance(
                    lat,
                    long,
                    vehicle.position.latitude,
                    vehicle.position.longitude
                )
                if (distance <= maxDistance) {
                    vehicleDataList.add(
                        VehicleData(
                            distace_frome_me = distance,
                            id = entity.id,
                            tripId = vehicle.trip.tripId,
                            startTime = vehicle.trip.startTime,
                            startDate = vehicle.trip.startDate,
                            scheduleRelationship = vehicle.trip.scheduleRelationship.name,
                            routeId = vehicle.trip.routeId,
                            latitude = vehicle.position.latitude,
                            longitude = vehicle.position.longitude,
                            speed = vehicle.position.speed,
                            timestamp = entity.vehicle.timestamp,
                            vehicleId = vehicle.vehicle.id,
                            vehicleLabel = vehicle.vehicle.label
                        )
                    )
                }
            }
        }

        return vehicleDataList
    }

    fun getNearbyStops(lat: Double, long: Double) {
        viewModelScope.launch {
            stopsUiState = try {
                Log.d("value", "$lat $long")
                val stopDetailsResponse = OneDelhiApi.retrofitService.getNearByStops(lat, long)
                val stopDetails = stopDetailsResponse.body()
                    ?.distinctBy { it.stop_code } // Remove duplicates by stopId
                fetchVehiclePositionsData(lat, long)

                StopsUiState.Success(
                    stopDetails
                )
            } catch (e: IOException) {
                Log.d("main", e.toString())
                StopsUiState.Error
            } catch (e: HttpException) {
                Log.d("main", e.toString())
                StopsUiState.Error
            }
        }
    }

}
