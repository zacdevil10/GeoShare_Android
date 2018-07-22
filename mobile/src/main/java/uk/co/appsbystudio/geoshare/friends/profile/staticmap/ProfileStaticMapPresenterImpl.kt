package uk.co.appsbystudio.geoshare.friends.profile.staticmap

import uk.co.appsbystudio.geoshare.utils.DownloadImageTask
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations

class ProfileStaticMapPresenterImpl(private var view: ProfileStaticMapView, private var interactor: ProfileStaticMapInteractor): ProfileStaticMapPresenter, ProfileStaticMapInteractor.OnFirebaseListener {

    override fun location(uid: String) {
        interactor.getLocation(uid, this)
    }

    override fun setImage(location: DatabaseLocations) {
        DownloadImageTask(location.lat, location.longitude, view).execute()
    }

    override fun error(error: String) {
        view.showError(error)
    }

}