package uk.co.appsbystudio.geoshare.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask

import java.io.InputStream
import java.net.URL

import uk.co.appsbystudio.geoshare.friends.profile.staticmap.ProfileStaticMapView

class DownloadImageTask(private val lat: Double, private val lng: Double, private val view: ProfileStaticMapView) : AsyncTask<Void, Void, Bitmap>() {

    override fun doInBackground(vararg voids: Void): Bitmap? {
        val url = "https://maps.googleapis.com/maps/api/staticmap?center=$lat,$lng&zoom=18&size=600x600&markers=color:0x4CAF50%7C$lat,$lng&key=AIzaSyB7fJe5C8nfedKovcp_oLe7hrYm9bRgMlU"
        val style = "&style=element:geometry|color:0xf5f5f5&style=element:labels.text.fill|color:0x616161&style=element:labels.text.stroke|color:0xf5f5f5&style=feature:administrative|saturation:-100&style=feature:administrative.land_parcel|element:labels.text.fill|color:0xbdbdbd&style=feature:landscape|saturation:-100&style=feature:poi|element:geometry|color:0xeeeeee&style=feature:poi|element:labels.text.fill|color:0x757575&style=feature:poi.park|element:geometry|color:0xe5e5e5&style=feature:poi.park|element:labels.text.fill|color:0x9e9e9e&style=feature:road|element:geometry|color:0xffffff&style=feature:road.arterial|element:labels.text.fill|color:0x757575&style=feature:road.highway|element:geometry|color:0xdadada&style=feature:road.highway|element:labels.text|visibility:simplified&style=feature:road.local|element:labels.text.fill|color:0x9e9e9e&style=feature:transit.line|element:geometry|color:0xe5e5e5&style=feature:transit.station|element:geometry|color:0xeeeeee&style=feature:water|element:geometry|color:0xc9c9c9&style=feature:water|element:labels.text.fill|color:0x9e9e9e"
        var staticMap: Bitmap? = null
        try {
            val inputStream = URL(url + style).openStream()
            staticMap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return staticMap
    }

    override fun onPostExecute(result: Bitmap) {
        view.setMapImage(result)
    }
}
