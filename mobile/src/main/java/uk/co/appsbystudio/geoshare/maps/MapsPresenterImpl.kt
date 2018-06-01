package uk.co.appsbystudio.geoshare.maps

import android.graphics.Bitmap
import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import java.util.*

class MapsPresenterImpl(private val mapsView: MapsView, private val mapsInteractor: MapsInteractor): MapsPresenter, MapsInteractor.OnFirebaseRequestFinishedListener {

    override fun getStaticFriends() {
        mapsInteractor.staticFriends(this)
    }

    override fun getTrackingFriends() {
        mapsInteractor.trackingFriends(this)
    }

    override fun setTrackingSync(sync: Boolean) {
        mapsInteractor.trackingSync(sync)
    }

    override fun moveMapCamera(latLng: LatLng, zoomLevel: Int, animated: Boolean) {
        mapsView.updateCameraPosition(latLng, zoomLevel, animated)
    }

    override fun updateNearbyFriendsCount(friendsMarkerList: Map<String?, Marker?>) {
        var count = 0

        //TODO: Update with shared preferences
        val radius = 200

        for (markerId in friendsMarkerList.keys) {
            val markerLocation = friendsMarkerList[markerId]?.position
            val tempLocation = Location("")

            tempLocation.latitude = markerLocation!!.latitude
            tempLocation.longitude = markerLocation.longitude

            //TODO: Get phone location here
            /*if (gpsTracking.getLocation().distanceTo(tempLocation) < radius) {
                count++
            }*/
        }

        mapsView.updateNearbyText(count)
    }

    override fun updateNearbyFriendsRadius(radius: Int, centerPoint: LatLng) {
        mapsView.updateNearbyRadiusCircle(radius, centerPoint)
    }

    override fun locationAdded(key: String?, markerPointer: Bitmap?, databaseLocations: DatabaseLocations?) {
        mapsView.addFriendMarker(key, markerPointer, databaseLocations)
    }

    override fun locationChanged(key: String?, databaseLocations: DatabaseLocations?) {
        mapsView.updateFriendMarker(key, databaseLocations)
    }

    override fun locationRemoved(key: String?) {
        mapsView.removeFriendMarker(key)
    }

    override fun markerExists(uid: String): Boolean {
        return mapsView.markerExists(uid)
    }

    override fun error(error: String) {
        mapsView.showError(error)
    }
}