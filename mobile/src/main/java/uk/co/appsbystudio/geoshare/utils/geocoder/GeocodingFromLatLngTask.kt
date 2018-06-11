package uk.co.appsbystudio.geoshare.utils.geocoder

import android.os.AsyncTask

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException

import uk.co.appsbystudio.geoshare.utils.downloadUrl
import uk.co.appsbystudio.geoshare.utils.getReverseGeocodingUrl

class GeocodingFromLatLngTask(private val lat: Double, private val lng: Double) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg voids: Void): String? {
        var finalAddress: String? = null

        val url = getReverseGeocodingUrl(lat, lng)
        try {
            val jsonResponse = downloadUrl(url)

            val jsonObject = JSONObject(jsonResponse)

            finalAddress = (jsonObject.get("results") as JSONArray).getJSONObject(0).getString("formatted_address")

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return finalAddress
    }
}