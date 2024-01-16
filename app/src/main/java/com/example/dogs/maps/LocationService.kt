package com.example.dogs.maps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationService {
    @SuppressLint("MissingPermission")
     suspend fun getUserLocation(context:Context):Location?{
        val fusedLocationProviderClient:FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        // verificar permisos
        val isUserLocationPermissionsGranted = true
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER )
        if(!isGPSEnable|| !isUserLocationPermissionsGranted){
            return null
        }
        return  suspendCancellableCoroutine { cont ->
            fusedLocationProviderClient.lastLocation.apply {
                if(isComplete){
                  if (isSuccessful){
                      cont.resume(result){}
                  }  else {
                      cont.resume(null){}
                  }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener { cont.resume(it){} }
                addOnFailureListener{ cont.resume(null){} }
                addOnCanceledListener { cont.resume(null){} }
            }
        }

    }

}