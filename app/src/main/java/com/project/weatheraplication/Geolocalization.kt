package com.project.weatheraplication

import android.Manifest
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import android.content.pm.PackageManager
import android.location.*

import androidx.core.app.ActivityCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import java.util.*


class Geolocalization() {

    private lateinit var criteria: Criteria
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    fun test(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        println(getLocation(context))
    }

    private fun getLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(OnCompleteListener {
            val location = it.result
            if (location != null) {
                val geo = Geocoder(context, Locale.getDefault())
                val address = geo.getFromLocation(location.latitude, location.longitude, 1)
                println(address[0].longitude)
                println(address[0].latitude)
            }
        })

    }
}