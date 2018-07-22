package uk.co.appsbystudio.geoshare.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.lang.Math.round

fun distance(startLatLng: LatLng, endLatLng: LatLng): String {
    val startLocation = Location("start")
    val endLocation = Location("end")

    startLocation.latitude = startLatLng.latitude
    startLocation.longitude = startLatLng.longitude

    endLocation.latitude = endLatLng.latitude
    endLocation.longitude = endLatLng.longitude

    return round(startLocation.distanceTo(endLocation)).distanceConverter()
}

private fun Int.distanceConverter(): String {
    if (this > 999) return "${this.div(1000)} KM"
    return "$this M"
}