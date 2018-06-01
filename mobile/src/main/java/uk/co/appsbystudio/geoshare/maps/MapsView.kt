package uk.co.appsbystudio.geoshare.maps

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations

interface MapsView {

    fun addFriendMarker(uid: String?, markerPointer: Bitmap?, databaseLocations: DatabaseLocations?)

    fun updateFriendMarker(uid: String?, databaseLocations: DatabaseLocations?)

    fun removeFriendMarker(uid: String?)

    fun markerExists(uid: String): Boolean

    fun setMarkerVisibility(uid: String, visible: Boolean)

    fun setAllMarkersVisibility(visible: Boolean)

    fun findOnMap()

    fun updateCameraPosition(latLng: LatLng, zoomLevel: Int, animated: Boolean)

    fun updateNearbyText(nearbyCount: Int)

    fun updateNearbyRadiusCircle(radius: Int, centerPoint: LatLng)

    fun showError(message: String)

}