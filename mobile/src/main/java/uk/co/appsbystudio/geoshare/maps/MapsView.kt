package uk.co.appsbystudio.geoshare.maps

import androidx.lifecycle.LiveData
import android.content.BroadcastReceiver
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

    fun setBottomSheetState(state: Int)

    fun setMapStyle(style: Int)

    fun updateTrackingButton(trackingState: Boolean)

    fun updateCameraPosition(latLng: LatLng, zoomLevel: Int, animated: Boolean)

    fun updateNearbyText(nearbyCount: Int)

    fun updateBottomSheetText(uid: String?, address: LiveData<String>, timestamp: String?, distance: String)

    fun updateNearbyRadiusCircle(radius: Int?, centerPoint: LatLng)

    fun updateRadiusCircleSize(radius: Int?)

    fun showError(message: String)

    fun registerNetworkReceiver(broadcastReceiver: BroadcastReceiver)

    fun unregisterNetworkReceiver(broadcastReceiver: BroadcastReceiver)

    fun networkAvailable()

    fun networkError(message: String)

}