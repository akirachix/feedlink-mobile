package com.feedlink.feedlink.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

class LocationManager(private val context: Context) {
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun RequestLocationPermission(
    onPermissionResult: (Pair<Double, Double>?) -> Unit,
    onPermissionRequestReady: (launchPermissionRequest: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val locationManager = remember { LocationManager(context) }
    val hasPermission = remember { mutableStateOf(locationManager.hasLocationPermission()) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (coarseLocationGranted || fineLocationGranted) {
            hasPermission.value = true
            fetchLocation(context, onPermissionResult)
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            onPermissionResult(null)
        }
    }

    val launchPermissionRequest = {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(Unit) {
        onPermissionRequestReady(launchPermissionRequest)
    }

    LaunchedEffect(Unit) {
        if (hasPermission.value) {
            fetchLocation(context, onPermissionResult)
        }
    }
}

private fun fetchLocation(
    context: Context,
    onLocationReceived: (Pair<Double, Double>?) -> Unit
) {
    try {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onLocationReceived(Pair(location.latitude, location.longitude))
                } else {
                    Toast.makeText(
                        context,
                        "Location is null. Please check your location settings.",
                        Toast.LENGTH_SHORT
                    ).show()
                    onLocationReceived(null)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context,
                    "Failed to obtain location: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                onLocationReceived(null)
            }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Exception: ${e.message}", Toast.LENGTH_SHORT).show()
        onLocationReceived(null)
    }
}