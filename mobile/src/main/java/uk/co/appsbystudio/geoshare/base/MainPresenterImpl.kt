package uk.co.appsbystudio.geoshare.base

import android.app.DialogFragment
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.utils.ProfileUtils
import uk.co.appsbystudio.geoshare.utils.SettingsPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.ShowMarkerPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class MainPresenterImpl(private val mainView: MainView, private val showMarkerPreferencesHelper: ShowMarkerPreferencesHelper,
                        private val trackingPreferencesHelper: TrackingPreferencesHelper, private val settingsPreferencesHelper: SettingsPreferencesHelper,
                        private val mainInteractor: MainInteractorImpl):
        MainPresenter, MainInteractor.OnFirebaseRequestFinishedListener {

    override fun getFriends() {
        mainInteractor.getFriends(this)
    }

    override fun getFriendsTrackingState() {
        mainInteractor.getTrackingState(this)
    }

    override fun setTrackingService() {
        Runnable {
            trackingPreferencesHelper.getAll()?.forEach { (_, value) ->
                if (value as Boolean) {
                    mainView.startTrackingServiceIntent()
                    return@Runnable
                }
            }
        }.run()
    }

    override fun setFriendSharingState(uid: String, state: Boolean) {
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.TRACKING}/$uid/${FirebaseHelper.TRACKING}/${FirebaseAuth.getInstance().currentUser?.uid}").removeValue()
                .addOnSuccessListener {
                    trackingPreferencesHelper.setTrackingState(uid, state)
                }.addOnFailureListener {
                    mainView.showError(it.message!!)
                }
    }

    override fun stopTrackingService() {
        mainView.stopTrackingServiceIntent()
    }

    override fun updateNavProfilePicture(view: CircleImageView?, storageDirectory: String) {
        ProfileUtils.setProfilePicture(FirebaseAuth.getInstance().currentUser?.uid, view, storageDirectory)
    }

    override fun updateNavDisplayName() {
        mainView.setDisplayName(FirebaseAuth.getInstance().currentUser?.displayName)
    }

    override fun setMarkerVisibilityState() {
        mainView.markerToggleState(showMarkerPreferencesHelper.getAllMarkersVisibilityState())
    }

    override fun swapFragment(fragment: Fragment) {
        mainView.showFragment(fragment)
    }

    override fun openDialog(dialog: DialogFragment, tag: String) {
        mainView.showDialog(dialog, tag)
    }

    override fun friends() {
        mainView.friendsIntent()
    }

    override fun settings() {
        mainView.settingsIntent()
    }

    override fun feedback() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.type = "text/plain"
        emailIntent.data = Uri.parse("mailto:support@appsbystudio.co.uk")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "GeoShare Feedback")

        mainView.feedbackIntent(emailIntent)
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

    override fun clearSharedPreferences() {
        settingsPreferencesHelper.clear()
        trackingPreferencesHelper.clear()
        showMarkerPreferencesHelper.clear()
    }

    override fun navDrawerState(open: Boolean) {
        if (open) {
            mainView.openNavDrawer()
        } else {
            mainView.closeNavDrawer()
        }
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