package uk.co.appsbystudio.geoshare.maps

import android.graphics.Bitmap
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import uk.co.appsbystudio.geoshare.utils.SettingsPreferencesResources
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations

class MapsPresenterImpl(private val mapsView: MapsView,
                        private val settingsResources: SettingsPreferencesResources?,
                        private val mapsInteractor: MapsInteractorImpl):
        MapsPresenter, MapsInteractor.OnFirebaseRequestFinishedListener {

    override fun getStaticFriends() {
        mapsInteractor.staticFriends(this)
    }

    override fun getTrackingFriends(storageDirectory: String?) {
        mapsInteractor.trackingFriends(storageDirectory, this)
    }

    override fun setTrackingSync(sync: Boolean) {
        mapsInteractor.trackingSync(sync)
    }

    override fun updateTrackingState(trackingState: Boolean) {
        mapsView.updateTrackingButton(trackingState)
    }

    override fun moveMapCamera(latLng: LatLng, zoomLevel: Int, animated: Boolean) {
        mapsView.updateCameraPosition(latLng, zoomLevel, animated)
    }

    override fun updateNearbyFriendsCount(latLng: LatLng, friendsMarkerList: Map<String?, Marker?>) {
        var count = 0

        val radius = settingsResources?.nearbyRadius()

        for (markerId in friendsMarkerList.keys) {
            val markerLocation = friendsMarkerList[markerId]?.position
            val tempLocation = Location("markerTempLocation")
            val myTempLocation = Location("myTempLocation")

            if (markerLocation != null) {
                tempLocation.latitude = markerLocation.latitude
                tempLocation.longitude = markerLocation.longitude
            }

            myTempLocation.latitude = latLng.latitude
            myTempLocation.longitude = latLng.longitude

            if (radius != null && myTempLocation.distanceTo(tempLocation) < radius) {
                count++
            }
        }

        mapsView.updateNearbyText(count)
    }

    override fun updateNearbyFriendsRadius(centerPoint: LatLng) {
        mapsView.updateNearbyRadiusCircle(settingsResources?.nearbyRadius(), centerPoint)
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