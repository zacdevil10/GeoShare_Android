package uk.co.appsbystudio.geoshare.maps

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

interface MapsPresenter {

    fun getStaticFriends()

    fun getTrackingFriends()

    fun setTrackingSync(sync: Boolean)

    fun updateTrackingState(trackingState: Boolean)

    fun moveMapCamera(latLng: LatLng, zoomLevel: Int, animated: Boolean)

    fun updateNearbyFriendsCount(friendsMarkerList: Map<String?, Marker?>)

    fun updateNearbyFriendsRadius(centerPoint: LatLng)

}