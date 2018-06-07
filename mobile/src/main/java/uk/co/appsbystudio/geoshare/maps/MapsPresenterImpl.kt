package uk.co.appsbystudio.geoshare.maps

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

class MapsPresenterImpl(private val mapsView: MapsView,
                        private val settingsHelper: SettingsPreferencesHelper?,
                        private val mapsHelper: MapsHelperImpl,
                        private val mapsInteractor: MapsInteractorImpl):
        MapsPresenter, MapsInteractor.OnFirebaseRequestFinishedListener, MapsHelper.OnNetworkStateChangedListener, MapsHelper.OnSharePreferencesChangedListener {

    override fun getStaticFriends() {
        mapsInteractor.staticFriends(this)
    }

    override fun getTrackingFriends(storageDirectory: String?) {
        mapsInteractor.trackingFriends(storageDirectory, this)
    }

    override fun setTrackingSync(sync: Boolean) {
        mapsInteractor.trackingSync(sync)
    }

    override fun networkTrackingSync(networkType: Int) {
        if (networkType == 0) {
            mapsInteractor.trackingSync(true)
        } else if (networkType == 1 && settingsHelper != null) {
            mapsInteractor.trackingSync(settingsHelper.mobileSyncState()!!)
        }
    }

    override fun updateTrackingState(trackingState: Boolean) {
        mapsView.updateTrackingButton(trackingState)
    }

    override fun moveMapCamera(latLng: LatLng, zoomLevel: Int, animated: Boolean) {
        mapsView.updateCameraPosition(latLng, zoomLevel, animated)
    }

    override fun updateNearbyFriendsCount(latLng: LatLng, friendsMarkerList: Map<String?, Marker?>) {
        var count = 0

        val radius = settingsHelper?.nearbyRadius()

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

    override fun updateMapStyle(nightTheme: Boolean) {
        if (nightTheme) {
            mapsView.setMapStyle(R.raw.map_style_dark)
        } else {
            mapsView.setMapStyle(R.raw.map_style)
        }
    }

    override fun updateBottomSheetState(state: Int) {
        mapsView.setBottomSheetState(state)
    }

    override fun updateBottomSheet(uid: String, startLatLng: LatLng, endLatLng: LatLng, timestamp: Long?) {
        mapsView.updateBottomSheetText(MainActivity.friendNames[uid], GeocodingFromLatLngTask(endLatLng.latitude, endLatLng.longitude).execute().get(), timestamp?.convertDate(), distance(startLatLng, endLatLng))
    }

    override fun updateNearbyFriendsRadius(centerPoint: LatLng) {
        mapsView.updateNearbyRadiusCircle(settingsHelper?.nearbyRadius(), centerPoint)
    }

    override fun setError(message: String) {
        mapsView.showError(message)
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

    override fun networkStateChanged(state: Int) {
        if (state == 0) {
            mapsView.networkError("No network connection detected")
        } else {
            mapsView.networkAvailable()
        }
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

    override fun registerNetworkReceiver(broadcastReceiver: BroadcastReceiver) {
        mapsView.registerNetworkReceiver(broadcastReceiver)
    }

    override fun available(state: Boolean) {
        if (state) {
            mapsView.networkAvailable()
        } else {
            mapsView.networkError("No network connection detected")
        }
    }

    override fun networkType(type: Int) {
        if (type == 0) {
            mapsInteractor.trackingSync(true)
        } else if (type == 1 && settingsHelper != null) {
            mapsInteractor.trackingSync(settingsHelper.mobileSyncState()!!)
        }
    }

    override fun unregisterNetworkReceiver(broadcastReceiver: BroadcastReceiver) {
        mapsView.unregisterNetworkReceiver(broadcastReceiver)
    }

    override fun updatedNearbyRadius() {
        mapsView.updateRadiusCircleSize(settingsHelper?.nearbyRadius())
    }
}