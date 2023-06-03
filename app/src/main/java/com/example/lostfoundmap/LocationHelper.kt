package com.example.lostfoundmap
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

class LocationHelper(private val context: Context) {

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationListener: LocationListener? = null

    interface LocationCallback {
        fun onLocationResult(location: Location)
        fun onLocationError(error: String)
    }

    fun getCurrentLocation(callback: LocationCallback) {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                callback.onLocationResult(location)
                stopLocationUpdates()
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {
                callback.onLocationError("Location provider disabled.")
                stopLocationUpdates()
            }
        }

        try {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,
                locationListener as LocationListener, null)
        } catch (e: SecurityException) {
            callback.onLocationError("Location permission not granted.")
        }
    }

    fun stopLocationUpdates() {
        locationListener?.let {
            locationManager.removeUpdates(it)
        }
    }
}
