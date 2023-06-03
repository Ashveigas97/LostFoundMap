package com.example.lostfoundmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        dbHelper = DatabaseHelper(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Check location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        // Add marker for lost and found items
        val itemLocations = dbHelper.getLostAndFoundItemLocationsFromDatabase()
        for (location in itemLocations) {
            val markerOptions = MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .title(location.title)
            map.addMarker(markerOptions)
        }

        // Move camera to a default location

        val melbourneLocation = LatLng(-37.8136, 144.9631)
        val markerOptions1 = MarkerOptions()
            .position(LatLng(-37.8136, 144.9631))
            .title("Melbourne CBD")
        val markerOptions2 = MarkerOptions()
            .position(LatLng(-33.8157, 151.0034))
            .title("Paramatta Sydney")
        val markerOptions3 = MarkerOptions()
            .position(LatLng(-38.2005, 144.3262))
            .title("Waurn Ponds")
        val markerOptions4 = MarkerOptions()
            .position(LatLng(-37.87923, 144.61635))
            .title("Wyndham Vale")
        map.addMarker(markerOptions1)
        map.addMarker(markerOptions2)
        map.addMarker(markerOptions3)
        map.addMarker(markerOptions4)


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourneLocation, 4f))


        map.setOnMarkerClickListener { marker ->
            true
        }


    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        map.isMyLocationEnabled = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
