package uk.co.appsbystudio.geoshare.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/* DIRECTIONS METHODS */
/*fun getDirectionsUrl(origin: LatLng, dest: LatLng) {
    val sOrigin = "origin=" + origin.latitude + "," + origin.longitude
    val sDest = "destination=" + dest.latitude + "," + dest.longitude

    val params = "$sOrigin&$sDest&sensor=false"

    Application.getContext().getString(R.string.server_key)
}*/

/* GEOCODING METHOD */

//Reverse Geocoding
fun getReverseGeocodingUrl(lat: Double, lng: Double): String {
    return "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lng&key=AIzaSyCMzZatGotT2XcOm5DOmKMBnybNZgRg1jQ"
}

//Download url result
fun downloadUrl(stringUrl: String): String {

    var data = ""
    val obj = URL(stringUrl)

    with(obj.openConnection() as HttpURLConnection) {
        requestMethod = "GET"

        BufferedReader(InputStreamReader(inputStream)).use {
            val response = StringBuffer()
            var inputLine = it.readLine()
            while (inputLine != null) {
                response.append(inputLine)
                inputLine = it.readLine()
            }
            data = response.toString()
        }
    }

    return data
}
