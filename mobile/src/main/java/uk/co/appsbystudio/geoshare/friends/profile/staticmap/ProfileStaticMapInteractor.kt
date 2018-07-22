package uk.co.appsbystudio.geoshare.friends.profile.staticmap

import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations

interface ProfileStaticMapInteractor {

    interface OnFirebaseListener {
        fun setImage(location: DatabaseLocations)
        fun error(error: String)
    }

    fun getLocation(uid: String, listener: OnFirebaseListener)
}