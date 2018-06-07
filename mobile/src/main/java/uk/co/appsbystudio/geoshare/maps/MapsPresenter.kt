package uk.co.appsbystudio.geoshare.maps

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

interface MapsPresenter {

    fun getStaticFriends()

    fun getTrackingFriends(storageDirectory: String?)

    fun setTrackingSync(sync: Boolean)

    fun networkTrackingSync(networkType: Int)

    fun updateTrackingState(trackingState: Boolean)

    fun moveMapCamera(latLng: LatLng, zoomLevel: Int, animated: Boolean)

    fun updateNearbyFriendsCount(latLng: LatLng, friendsMarkerList: Map<String?, Marker?>)

    fun updateMapStyle(nightTheme: Boolean)

    fun updateBottomSheetState(state: Int)

    fun updateBottomSheet(uid: String, startLatLng: LatLng, endLatLng: LatLng, timestamp: Long?)

    fun updateNearbyFriendsRadius(centerPoint: LatLng)

    fun setError(message: String)

    fun registerNetworkReceiver()

    fun unregisterNetworkReceiver()

    fun networkStateChanged(state: Int)

    fun registerSettingsPreferencesListener()

    fun unregisterSettingsPreferencesListener()

}