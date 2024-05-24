package com.example.mc_project.network

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.mc_project.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun CurrentLocationContent(navController: NavController,
    usePreciseLocation: Boolean, onLocationInfoAvailable: (List<Double>) -> Unit
) {
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationInfo = remember { mutableStateListOf<Double>() }
    val locationObtained = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Scaffold(
        containerColor = Color.White
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (image, button) = createRefs()

            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()){
                Image(
                    painter = painterResource(id = R.drawable.bg),
                    contentDescription = "",
                )

            }

            Column(modifier = Modifier.constrainAs(button) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {

                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                val location = locationClient.getCurrentLocation(
                                    Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token
                                )
                                location.addOnSuccessListener { location ->
                                    locationInfo.clear()
                                    locationInfo.addAll(
                                        listOf(
                                            location.latitude, location.longitude
                                        )
                                    )
                                    onLocationInfoAvailable(locationInfo)
                                    locationObtained.value = true
                                }.addOnFailureListener { exception ->
                                    locationInfo.clear()
                                    locationInfo.add(0.0)
                                    locationInfo.add(0.0)
                                    locationObtained.value = true
                                }
                            } catch (e: Exception) {
                                Log.d("location", e.toString())
                            }
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Get Current Location")
                }
                Text(
                    text = "Or",
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.padding(0.dp,5.dp,0.dp,0.dp) )
                Button(
                    onClick = {
                        navController.navigate("pick")
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.95f)
                        .padding(bottom = 15.dp)
                ) {
                    Text("Enter manually")
                }
            }
        }


    }
}
/*
@Composable
fun ComposeMapCenterPointMapMarker() {
    val markerPosition = LatLng(1.35, 103.87)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 18f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
            },
        ) {
            Image(
                painter = painterResource(id = R.drawable.pin),
                contentDescription = "marker",
            )
        }
        Text(text = "Is camera moving: ${cameraPositionState.isMoving}" +
                "\n Latitude and Longitude: ${cameraPositionState.position.target.latitude} " +
                "and ${cameraPositionState.position.target.longitude}",
            textAlign = TextAlign.Center
        )
    }
}*/





//
//@Composable
//fun ComposeMapCenterPointMapMarker() {
//
//}