package uk.co.appsbystudio.geoshare.friends.profile.info

import android.arch.lifecycle.MutableLiveData
import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.convertDate
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.geocoder.GeocodingFromLatLngTask

class ProfileInfoPresenterImpl(private val view: ProfileInfoView, private val trackingPreferencesHelper: TrackingPreferencesHelper?,
                               private val interactor: ProfileInfoInteractor): ProfileInfoPresenter, ProfileInfoInteractor.TrackingStateChangeListener {

    override fun updateCurrentLocation(uid: String) {
        interactor.getLocation(uid, this)
    }

    override fun updateTrackingState(uid: String) {
        if (trackingPreferencesHelper?.getTrackingState(uid) != null && trackingPreferencesHelper.getTrackingState(uid)!!) {
            view.setShareText("Stop sharing")
        } else {
            view.setShareText("Share current location")
        }
    }

    override fun requestLocation(uid: String) {
        interactor.sendLocationRequest(uid, this)
    }

    override fun shareLocation(uid: String) {
        if (trackingPreferencesHelper?.getTrackingState(uid) != null && trackingPreferencesHelper.getTrackingState(uid)!!) {
            interactor.stopSharing(uid, this)
        } else {
            view.showShareDialog()
        }
    }

    override fun removeLocation(uid: String) {
        interactor.deleteLocation(uid, this)
    }

    override fun updateShareState(uid: String, text: String) {
        trackingPreferencesHelper?.setTrackingState(uid, false)
        view.setShareText(text)
    }

    override fun updateLocationText(location: DatabaseLocations) {
        val liveData = MutableLiveData<String>()
        GeocodingFromLatLngTask(location.lat, location.longitude, liveData).execute()
        with (location){ view.setLocationItemText(liveData, timestamp.convertDate()) }
    }

    override fun resetLocationText() {
        view.setLocationItemText()
    }

    override fun updateDeleteState(visible: Int) {
        view.deleteButtonVisibility(visible)
    }

    override fun success(message: String) {
        view.showToast(message)
    }

    override fun error(error: String) {
        view.showToast(error)
    }
}