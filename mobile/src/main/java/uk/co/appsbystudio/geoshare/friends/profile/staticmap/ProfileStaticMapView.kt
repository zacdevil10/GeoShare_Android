package uk.co.appsbystudio.geoshare.friends.profile.staticmap

import android.graphics.Bitmap

interface ProfileStaticMapView {

    fun setMapImage(bitmap: Bitmap)

    fun showError(error: String)
}