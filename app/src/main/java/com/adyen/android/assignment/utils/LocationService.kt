package com.adyen.android.assignment.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient

class LocationService(private val fragment: Fragment) {
    var latitude: Double? = null
    var longitude: Double? = null
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>

    fun isLocationOn(locationManager: LocationManager): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setLocationPermission(granted: () -> Unit, notGranted: () -> Unit) {
        locationPermissionRequest = fragment.registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                         granted()
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        granted()
                    } else -> {
                        notGranted()
                    }
                }
            }
    }

    fun requestPermission() {
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    fun getLocation(fusedLocationProviderClient: FusedLocationProviderClient,
                    success: () -> Unit, failure: () -> Unit, context: Context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener {
                    if(it != null) {
                        latitude = it.latitude
                        longitude = it.longitude
                        success()
                    } else {
                        failure()
                        Toast.makeText(context, "Something went wrong when getting location", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    failure()
                    Toast.makeText(context, "Something went wrong when getting location", Toast.LENGTH_SHORT).show()
                }

        }
    }
}