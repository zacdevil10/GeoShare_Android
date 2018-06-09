package uk.co.appsbystudio.geoshare.friends.profile.info

interface ProfileInfoPresenter {

    fun updateCurrentLocation(uid: String)

    fun updateTrackingState(uid: String)

    fun requestLocation(uid: String)

    fun shareLocation(uid: String)

    fun removeLocation(uid: String)
}