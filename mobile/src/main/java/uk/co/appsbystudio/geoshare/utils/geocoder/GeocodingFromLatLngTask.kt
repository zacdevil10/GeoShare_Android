package uk.co.appsbystudio.geoshare.utils.geocoder

import android.arch.lifecycle.MutableLiveData
import android.os.AsyncTask
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import uk.co.appsbystudio.geoshare.utils.downloadUrl
import uk.co.appsbystudio.geoshare.utils.getReverseGeocodingUrl
import java.io.IOException

class GeocodingFromLatLngTask(private val lat: Double, private val lng: Double, private val liveData: MutableLiveData<String>? = null) : AsyncTask<Void, Void, String>() {

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

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        if (liveData != null && result != null && !result.isBlank()) {
            liveData.value = result
            return
        }

        liveData?.value = "Could not get address..."
    }
}