package uk.co.appsbystudio.geoshare.friends.profile.info

import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations

interface ProfileInfoInteractor {

    interface TrackingStateChangeListener {
        fun updateShareState(uid: String, text: String)
        fun updateLocationText(location: DatabaseLocations)
        fun resetLocationText()
        fun updateDeleteState(visible: Int)
        fun success(message: String)
        fun error(error: String)
    }

    fun getLocation(uid: String, listener: TrackingStateChangeListener)

    fun deleteLocation(uid: String, listener: TrackingStateChangeListener)

    fun sendLocationRequest(uid: String, listener: TrackingStateChangeListener)

    fun stopSharing(uid: String, listener: TrackingStateChangeListener)
}