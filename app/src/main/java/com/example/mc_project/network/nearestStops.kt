package com.example.mc_project.network

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.example.mc_project.viewModels.ApiViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.mc_project.R
import com.example.mc_project.StopsDetails
import com.example.mc_project.viewModels.StopsUiState
import com.example.mc_project.viewModels.buesyouarein
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import android.graphics.BitmapFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable

fun NearestStopsScreen(
    navController: NavHostController,
    viewModel: ApiViewModel,
    lat: Double,
    lon: Double,
    context: Context
) {
    val modifier = Modifier
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                actions = {
                    IconButton(onClick = { }) {}
                    Icon(Icons.Default.MoreVert, contentDescription = "", tint = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "", tint = Color.Black)

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("MERI BUS", color = MaterialTheme.colorScheme.background)
                }
            )
        }
    ) { paddingValues ->
        val stopsUiState = viewModel.stopsUiState
        val busState = viewModel.bus
        viewModel.getNearbyStops(lat, lon)
        Column(modifier.padding(paddingValues)) {

            when (stopsUiState) {
                is StopsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                is StopsUiState.Success -> {
                    when (busState) {
                        is buesyouarein.Loading -> {
                            LoadingScreen(modifier = modifier.fillMaxSize())
                        }

                        is buesyouarein.Success -> {
                            val busList = busState.buesyouarein ?: emptyList()
                            val closestBus = busList.minByOrNull { it.distace_frome_me }

                            closestBus?.let { closestBus ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Bus ID: ${closestBus.id}",
                                            style = TextStyle(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 18.sp
                                            )
                                        )
                                        Text(
                                            text = "Start Date: ${closestBus.startDate}",
                                            style = TextStyle(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 16.sp
                                            )
                                        )
                                        Text(
                                            text = "Start Time: ${closestBus.startTime}",
                                            style = TextStyle(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 16.sp
                                            )
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .padding(15.dp)
                                        .requiredHeight(500.dp)
                                ) {
                                    MapWithMarkers(
                                        closestBus,
                                        stopsUiState.stopDetails ?: emptyList(),
                                        context
                                    )
                                }
                            } ?: run {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "No Bus Around You In 500m Radius",
                                            style = TextStyle(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 18.sp
                                            )
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .padding(15.dp)
                                        .requiredHeight(500.dp)
                                ) {
                                    MapWithMarkers(
                                        closestBus,
                                        stopsUiState.stopDetails ?: emptyList(),
                                        context
                                    )
                                }
                            }
                        }

                        else -> ErrorScreen("error")
                    }

                    val stopsDetails = stopsUiState.stopDetails
                    if (stopsDetails.isNullOrEmpty()) {
                        ErrorScreen("Error in result")
                    } else {
                        val sortedStops =
                            stopsDetails.sortedBy { it.distance } // Sort stops by distance

                        LazyRow() {
                            items(sortedStops) { stop ->
                                Card(Modifier.padding(4.dp)) { StopItem(stop = stop) }
                            }
                        }
                    }
                }

                else -> {
                    Text(text = "Stop error")
                }
            }

        }
    }
}


@Composable
fun MapWithMarkers(
    busData: VehicleData?,
    stops: List<StopsDetails>,
    context: Context
) {
    // Calculate the average latitude and longitude of all stops
    val averageLatitude = stops.map { it.stop_lat }.average()
    val averageLongitude = stops.map { it.stop_lon }.average()
    val markerPosition = LatLng(averageLatitude, averageLongitude)

    // Remember the camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 16f)
    }

    // Show GoogleMap
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Add markers for each bus
        val busLatitude = busData?.latitude
        val busLongitude = busData?.longitude
        val busMarkerState = rememberMarkerState("Bus marker")
        if (busLatitude != null) {
            if (busLongitude != null) {
                busMarkerState.position = LatLng(busLatitude.toDouble(), busLongitude.toDouble())
            }
        }
        Marker(
            state = busMarkerState,
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
            anchor = Offset(0.5f, 0.5f)
        )

        // Add markers for each stop
        stops.forEach { stop ->
            val stopMarkerState = rememberMarkerState("Stop marker")
            stopMarkerState.position = LatLng(stop.stop_lat.toDouble(), stop.stop_lon.toDouble())
            Marker(
                state = stopMarkerState,
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                anchor = Offset(0.5f, 0.5f)
            )
        }
    }
}


@Composable
fun StopItem(stop: StopsDetails) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
//            .background(Color.Black.copy(alpha = 0.7f)) // 70% transparency
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = stop.stop_name,
                style = TextStyle(color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
            )
            Text(
                text = "lat: ${stop.stop_lat} m",
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
            )
            Text(
                text = "lon: ${stop.stop_lon} m",
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
            )
        }
    }
}
