package uk.co.appsbystudio.geoshare.friends.manager.pages.current

import uk.co.appsbystudio.geoshare.utils.ShowMarkerPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper

class FriendsPresenterImpl(private val view: FriendsView, private val interactor: FriendsInteractor,
                           private val trackingPreferencesHelper: TrackingPreferencesHelper,
                           private val showMarkerPreferencesHelper: ShowMarkerPreferencesHelper): FriendsPresenter, FriendsInteractor.OnFirebaseListener {

    override fun friends() {
        interactor.getFriends(this)
    }

    override fun removeFriend(uid: String) {
        interactor.removeFriend(uid, this)
    }

    override fun stop() {
        interactor.removeListener()
    }

    override fun add(uid: String?) {
        if (uid != null) {
            view.addFriend(uid)
        }
    }

    override fun remove(uid: String?) {
        if (uid != null) {
            view.removeFriend(uid)
        }
    }

    override fun unfriended(uid: String) {
        if (trackingPreferencesHelper.exists(uid)!!) trackingPreferencesHelper.removeEntry(uid)
        if (showMarkerPreferencesHelper.exists(uid)!!) showMarkerPreferencesHelper.removeEntry(uid)
    }

    override fun error(error: String) {
        view.showToast(error)
    }
}