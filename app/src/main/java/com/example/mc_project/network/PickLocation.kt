package com.example.mc_project.network

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mc_project.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
@Composable
fun LocationPick(
    navController: NavHostController
) {
    val averageLatitude = 28.70405920
    val averageLongitude = 77.10249020
    val markerPosition = remember { mutableStateOf(LatLng(averageLatitude, averageLongitude)) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition.value, 12f)
    }
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth(),
        cameraPositionState = cameraPositionState
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        Text(
            text = "Is camera moving: ${cameraPositionState.isMoving}" +
                    "\n Latitude : ${cameraPositionState.position.target.latitude} " +
                    "\n Longitude: ${cameraPositionState.position.target.longitude}",
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.pin),
            contentDescription = "marker",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                // Save latitude and longitude of the marker's current position
                val latitude = cameraPositionState.position.target.latitude
                val longitude = cameraPositionState.position.target.longitude

                navController.navigate("nearestStops/${latitude}/${longitude}")
            },
            modifier = Modifier
                .padding(16.dp)
                .height(48.dp)
        ) {
            Text(text = "Save Location and Find Nearest Stops")
        }
    }
}

