package uk.co.appsbystudio.geoshare.maps

import android.arch.lifecycle.MutableLiveData
import android.content.BroadcastReceiver
import android.graphics.Bitmap
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.utils.SettingsPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.convertDate
import uk.co.appsbystudio.geoshare.utils.distance
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.geocoder.GeocodingFromLatLngTask

class MapsPresenterImpl(private val view: MapsView,
                        private val settingsPreferencesHelper: SettingsPreferencesHelper?,
                        private val mapsHelper: MapsHelperImpl,
                        private val interactor: MapsInteractorImpl):
        MapsPresenter, MapsInteractor.OnFirebaseRequestFinishedListener, MapsHelper.OnNetworkStateChangedListener, MapsHelper.OnSharePreferencesChangedListener {

    private val liveData = MutableLiveData<String>()

    override fun getStaticFriends(storageDirectory: String?) {
        interactor.staticFriends(storageDirectory, this)
    }

    override fun getTrackingFriends(storageDirectory: String?) {
        interactor.trackingFriends(storageDirectory, this)
    }

    override fun setTrackingSync(sync: Boolean): Boolean {
        return interactor.trackingSync(sync)
    }

    override fun updateTrackingState(trackingState: Boolean) {
        view.updateTrackingButton(trackingState)
    }

    override fun moveMapCamera(latLng: LatLng, zoomLevel: Int, animated: Boolean) {
        view.updateCameraPosition(latLng, zoomLevel, animated)
    }

    override fun updateNearbyFriendsCount(latLng: LatLng, friendsMarkerList: Map<String?, Marker?>) {
        var count = 0

        val radius = settingsPreferencesHelper?.nearbyRadius()

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

        view.updateNearbyText(count)
    }

    override fun updateMapStyle(nightTheme: Boolean) {
        if (nightTheme) {
            view.setMapStyle(R.raw.map_style_dark)
        } else {
            view.setMapStyle(R.raw.map_style)
        }
    }

    override fun updateBottomSheetState(state: Int) {
        view.setBottomSheetState(state)
    }

    override fun updateBottomSheet(uid: String, startLatLng: LatLng, endLatLng: LatLng, timestamp: Long?) {
        GeocodingFromLatLngTask(endLatLng.latitude, endLatLng.longitude, liveData).execute()
        view.updateBottomSheetText(MainActivity.friendNames[uid], liveData, timestamp?.convertDate(), distance(startLatLng, endLatLng))
    }

    override fun updateNearbyFriendsRadius(centerPoint: LatLng) {
        view.updateNearbyRadiusCircle(settingsPreferencesHelper?.nearbyRadius(), centerPoint)
    }

    override fun setError(message: String) {
        view.showError(message)
    }

    override fun registerNetworkReceiver() {
        mapsHelper.registerNetworkReceiver(this)
    }

    override fun unregisterNetworkReceiver() {
        mapsHelper.unregisterNetworkReceiver(this)
    }

    override fun registerSettingsPreferencesListener() {
        mapsHelper.registerSharedPreferencesListener(this)
    }

    override fun unregisterSettingsPreferencesListener() {
        mapsHelper.unregisterSharedPreferencesListener()
    }

    override fun locationAdded(key: String?, markerPointer: Bitmap?, databaseLocations: DatabaseLocations?) {
        view.addFriendMarker(key, markerPointer, databaseLocations)
    }

    override fun locationChanged(key: String?, databaseLocations: DatabaseLocations?) {
        view.updateFriendMarker(key, databaseLocations)
    }

    override fun locationRemoved(key: String?) {
        view.removeFriendMarker(key)
    }

    override fun markerExists(uid: String): Boolean {
        return view.markerExists(uid)
    }

    override fun error(error: String) {
        view.showError(error)
    }

    override fun registerNetworkReceiver(broadcastReceiver: BroadcastReceiver) {
        view.registerNetworkReceiver(broadcastReceiver)
    }

    override fun available(state: Boolean) {
        if (state) {
            view.networkAvailable()
        } else {
            view.networkError("No network connection detected")
        }
    }

    override fun networkType(type: Int) {
        if (type == 0) {
            interactor.trackingSync(true)
        } else if (type == 1 && settingsPreferencesHelper != null) {
            interactor.trackingSync(settingsPreferencesHelper.mobileSyncState()!!)
        }
    }

    override fun unregisterNetworkReceiver(broadcastReceiver: BroadcastReceiver) {
        view.unregisterNetworkReceiver(broadcastReceiver)
    }

    override fun updatedNearbyRadius() {
        view.updateRadiusCircleSize(settingsPreferencesHelper?.nearbyRadius())
    }
}