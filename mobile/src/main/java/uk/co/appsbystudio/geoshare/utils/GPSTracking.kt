package uk.co.appsbystudio.geoshare.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

import com.google.android.gms.maps.model.LatLng

class GPSTracking(private val context: Context?) : LocationListener {

    private var location: Location? = null
    private var locationManager: LocationManager? = null
    private var latitude: Double = 51.512467
    private var longitude: Double = -0.093265

    val latLng: LatLng get() = LatLng(latitude, longitude)

    init {
        setLocation()
    }

    private fun setLocation() {
        try {
            locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var gpsEnabled = false
            var networkEnabled = false
            if (locationManager != null) {
                gpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                networkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            }

            if (networkEnabled) {
                setLocationManager(LocationManager.NETWORK_PROVIDER)
            }
            if (gpsEnabled) {
                if (location == null) {
                    setLocationManager(LocationManager.GPS_PROVIDER)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("MissingPermission")
    private fun setLocationManager(provider: String) {
        locationManager?.requestLocationUpdates(provider, TIME_TO_UPDATE, DISTANCE_TO_CHANGE.toFloat(), this)
        location = locationManager?.getLastKnownLocation(provider)
        if (location != null && location is Location) {
            latitude = location!!.latitude
            longitude = location!!.longitude
        }
    }

    fun getLatitude(): Double {
        if (location != null) latitude = location!!.latitude
        return latitude
    }

    fun getLongitude(): Double {
        if (location != null) longitude = location!!.longitude
        return longitude
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

    }

    override fun onProviderEnabled(s: String) {

    }

    override fun onProviderDisabled(s: String) {

    }

    companion object {
        private const val DISTANCE_TO_CHANGE: Long = 0
        private const val TIME_TO_UPDATE: Long = 500
    }
}
