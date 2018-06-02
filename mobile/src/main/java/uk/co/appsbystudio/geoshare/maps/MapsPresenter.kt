package uk.co.appsbystudio.geoshare.maps

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

interface MapsPresenter {

    fun getStaticFriends()

    fun getTrackingFriends(storageDirectory: String?)

    fun setTrackingSync(sync: Boolean)

    fun updateTrackingState(trackingState: Boolean)

    fun moveMapCamera(latLng: LatLng, zoomLevel: Int, animated: Boolean)

    fun updateNearbyFriendsCount(latLng: LatLng, friendsMarkerList: Map<String?, Marker?>)

    fun updateNearbyFriendsRadius(centerPoint: LatLng)

}