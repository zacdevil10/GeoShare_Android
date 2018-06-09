package uk.co.appsbystudio.geoshare.friends.profile.staticmap

import uk.co.appsbystudio.geoshare.utils.DownloadImageTask
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations

class ProfileStaticMapPresenterImpl(private var profileStaticMapView: ProfileStaticMapView, private var profileStaticMapInteractor: ProfileStaticMapInteractor): ProfileStaticMapPresenter, ProfileStaticMapInteractor.OnFirebaseListener {

    override fun location(uid: String) {
        profileStaticMapInteractor.getLocation(uid, this)
    }

    override fun setImage(location: DatabaseLocations) {
        DownloadImageTask("https://maps.googleapis.com/maps/api/staticmap?center=${location.lat},${location.longitude}&zoom=18&size=600x600&markers=color:0x4CAF50%7C${location.lat},${location.longitude}&key=AIzaSyB7fJe5C8nfedKovcp_oLe7hrYm9bRgMlU", profileStaticMapView).execute()
    }

    override fun error(error: String) {
        profileStaticMapView.showError(error)
    }

}