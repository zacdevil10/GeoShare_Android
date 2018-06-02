package uk.co.appsbystudio.geoshare.base

import android.support.v4.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class MainPresenterImpl(private val mainView: MainView,
                        private val mainInteractor: MainInteractorImpl): MainPresenter, MainInteractor.OnFirebaseRequestFinishedListener {

    override fun getFriends() {
        mainInteractor.getFriends(this)
    }

    override fun getFriendsTrackingState() {
        mainInteractor.getTrackingState(this)
    }

    override fun showFragment(fragment: Fragment) {
        mainView.swapFragment(fragment)
    }

    override fun friends() {
        mainView.friendsIntent()
    }

    override fun settings() {
        mainView.settingsIntent()
    }

    override fun feedback() {
        mainView.feedbackIntent()
    }

    override fun logout() {
        if (FirebaseAuth.getInstance() != null) {
            mainInteractor.removeToken()
            FirebaseAuth.getInstance().signOut()
        } else {
            mainView.showErrorSnackbar("Could not log out!")
        }
    }

    override fun auth() {
        mainView.logoutIntent()
    }

    override fun friendAdded(key: String?, name: String?) {
        mainView.updateFriendsList(key, name)
    }

    override fun friendRemoved(key: String?) {
        mainView.removeFromFriendList(key)
    }

    override fun trackingAdded(key: String?, trackingState: Boolean?) {
        mainView.updateTrackingState(key, trackingState)
    }

    override fun trackingRemoved(key: String?) {
        mainView.removeTrackingState(key)
    }

    override fun error(error: String) {
        mainView.showError(error)
    }
}