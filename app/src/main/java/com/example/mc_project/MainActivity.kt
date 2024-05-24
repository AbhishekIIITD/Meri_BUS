package com.example.mc_project

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mc_project.network.CurrentLocationContent
import com.example.mc_project.network.LocationPick
import com.example.mc_project.ui.theme.Mc_projectTheme
import com.example.mc_project.network.NearestStopsScreen
import com.example.mc_project.viewModels.ApiViewModel
import com.google.android.gms.location.FusedLocationProviderClient

class MainActivity : ComponentActivity() {

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
        setContent {
            Mc_projectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val apiViewModel: ApiViewModel= viewModel()
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(navController)
                        }
                        composable("pick") {
                            LocationPick(navController)
                        }

                        composable(
                            route = "nearestStops/{latitude}/{longitude}",
                            arguments = listOf(
                                navArgument("latitude") { type = NavType.StringType },
                                navArgument("longitude") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val latitudeStr = backStackEntry.arguments?.getString("latitude")
                            val longitudeStr = backStackEntry.arguments?.getString("longitude")
                            val latitude = latitudeStr?.toDoubleOrNull() ?: 0.0
                            val longitude = longitudeStr?.toDoubleOrNull() ?: 0.0
                            NearestStopsScreen(navController,apiViewModel,latitude, longitude,applicationContext)
                        }

                    }
                }
            }
        }
    }
}

@Composable
@SuppressLint("MissingPermission")
fun MainScreen(navController: NavHostController) {

    val locationInfo = remember { mutableStateListOf<Double>() }

    CurrentLocationContent(navController,
        usePreciseLocation = true,
        onLocationInfoAvailable = { info ->
            locationInfo.clear()
            locationInfo.addAll(info)
            navController.navigate("nearestStops/${locationInfo[0]}/${locationInfo[1]}")
        }
    )
}





