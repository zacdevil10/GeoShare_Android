package uk.co.appsbystudio.geoshare.friends.profile.info

import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.convertDate
import uk.co.appsbystudio.geoshare.utils.ellipsize
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.geocoder.GeocodingFromLatLngTask

class ProfileInfoPresenterImpl(private val profileInfoView: ProfileInfoView,
                               private val trackingPreferencesHelper: TrackingPreferencesHelper?,
                               private val profileInfoInteractor: ProfileInfoInteractor): ProfileInfoPresenter, ProfileInfoInteractor.TrackingStateChangeListener {

    override fun updateCurrentLocation(uid: String) {
        profileInfoInteractor.getLocation(uid, this)
    }

    override fun updateTrackingState(uid: String) {
        if (trackingPreferencesHelper?.getTrackingState(uid) != null && trackingPreferencesHelper.getTrackingState(uid)!!) {
            profileInfoView.setShareText("Stop sharing")
        } else {
            profileInfoView.setShareText("Share current location")
        }
    }

    override fun requestLocation(uid: String) {
        profileInfoInteractor.sendLocationRequest(uid, this)
    }

    override fun shareLocation(uid: String) {
        if (trackingPreferencesHelper?.getTrackingState(uid) != null && trackingPreferencesHelper.getTrackingState(uid)!!) {
            profileInfoInteractor.stopSharing(uid, this)
        } else {
            profileInfoView.showShareDialog()
        }
    }

    override fun removeLocation(uid: String) {
        profileInfoInteractor.deleteLocation(uid, this)
    }

    override fun updateShareState(uid: String, text: String) {
        trackingPreferencesHelper?.setTrackingState(uid, false)
        profileInfoView.setShareText(text)
    }

    override fun updateLocationText(location: DatabaseLocations) {
        with (location){ profileInfoView.setLocationItemText(GeocodingFromLatLngTask(lat, longitude).execute().get().ellipsize(43), timestamp.convertDate()) }
    }

    override fun resetLocationText() {
        profileInfoView.setLocationItemText()
    }

    override fun updateDeleteState(visible: Int) {
        profileInfoView.deleteButtonVisibility(visible)
    }

    override fun success(message: String) {
        profileInfoView.showToast(message)
    }

    override fun error(error: String) {
        profileInfoView.showToast(error)
    }
}