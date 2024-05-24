package com.example.mc_project.network

import com.example.mc_project.Database.StopInfo
import com.example.mc_project.StopsDetails
import com.google.transit.realtime.GtfsRealtime
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.ByteArrayInputStream

private const val BASE_URL_1 = "https://lol1221.pythonanywhere.com/api/"
private const val BASE_URL_2 = "https://otd.delhi.gov.in/api/realtime/"

private val retrofit1 = Retrofit.Builder()
    .baseUrl(BASE_URL_1)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private val retrofit2 = Retrofit.Builder()
    .baseUrl(BASE_URL_2)
    .addConverterFactory(ProtoConverterFactory.create())
    .build()

interface OneDelhiApiService {
            @GET("getnearbystops")
    suspend fun getNearByStops(@Query("lat") lat: Double, @Query("lon") lon: Double): Response<List<StopsDetails>>
}

interface VehiclePositionsService {
    @GET("VehiclePositions.pb")
    suspend fun getVehiclePositionsData(@Query("key") apiKey: String): Response<ResponseBody>
}

object OneDelhiApi {
    val retrofitService = retrofit1.create(OneDelhiApiService::class.java)
}

object VehiclePositionsApi {
    val retrofitService = retrofit2.create(VehiclePositionsService::class.java)
}

// Example usage

