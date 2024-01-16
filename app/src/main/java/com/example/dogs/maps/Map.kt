package com.example.dogs.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.dogs.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class Map : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private val locationS: LocationService = LocationService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        createMarker()
    }

    private fun createMarker() {
        var coordinates = LatLng(7.1, 7.1)
        lifecycleScope.launch {
            val result = locationS.getUserLocation(this@Map)
            if (result != null) {
                coordinates = LatLng(result.latitude, result.longitude)
            }
            val marker: MarkerOptions = MarkerOptions().position(coordinates).title("aqui estoy")
            mGoogleMap?.addMarker(marker)
            mGoogleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
                4000, null
            )
        }

    }
}