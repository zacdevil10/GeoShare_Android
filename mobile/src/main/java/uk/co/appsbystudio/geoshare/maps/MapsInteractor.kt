package uk.co.appsbystudio.geoshare.maps

import android.graphics.Bitmap
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations

interface MapsInteractor {

    interface OnFirebaseRequestFinishedListener {

        fun locationAdded(key: String?, markerPointer: Bitmap?, databaseLocations: DatabaseLocations?)

        fun locationChanged(key: String?, databaseLocations: DatabaseLocations?)

        fun locationRemoved(key: String?)

        fun markerExists(uid: String): Boolean

        fun error(error: String)
    }

    fun staticFriends(listener: MapsInteractor.OnFirebaseRequestFinishedListener)

    fun trackingFriends(listener: MapsInteractor.OnFirebaseRequestFinishedListener)

    fun trackingSync(sync: Boolean)

}