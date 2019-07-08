package uk.co.appsbystudio.geoshare.base

import android.app.DialogFragment
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.utils.SettingsPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.ShowMarkerPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.setProfilePicture

class MainPresenterImpl(private val view: MainView, private val markerPreferencesHelper: ShowMarkerPreferencesHelper,
                        private val trackingPreferencesHelper: TrackingPreferencesHelper, private val settingsPreferencesHelper: SettingsPreferencesHelper,
                        private val interactor: MainInteractorImpl):
        MainPresenter, MainInteractor.OnFirebaseRequestFinishedListener {

    override fun getFriends() {
        interactor.getFriends(this)
    }

    override fun getFriendsTrackingState() {
        interactor.getTrackingState(this)
    }

    override fun updatedProfileListener() {
        interactor.setUpdatedProfileListener(this)
    }

    override fun setTrackingService() {
        Runnable {
            trackingPreferencesHelper.getAll()?.forEach { (_, value) ->
                if (value as Boolean) {
                    view.startTrackingServiceIntent()
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
                    view.showError(it.message!!)
                }
    }

    override fun stopTrackingService() {
        view.stopTrackingServiceIntent()
    }

    override fun updateNavProfilePicture(view: CircleImageView?, storageDirectory: String) {
        view.setProfilePicture(FirebaseAuth.getInstance().currentUser?.uid, storageDirectory)
    }

    override fun updateNavDisplayName() {
        view.setDisplayName(FirebaseAuth.getInstance().currentUser?.displayName)
    }

    override fun setMarkerVisibilityState() {
        view.markerToggleState(markerPreferencesHelper.getAllMarkersVisibilityState())
    }

    override fun swapFragment(fragment: Fragment) {
        view.showFragment(fragment)
    }

    override fun openDialog(dialog: DialogFragment, tag: String) {
        view.showDialog(dialog, tag)
    }

    override fun friends() {
        view.friendsIntent()
    }

    override fun settings() {
        view.settingsIntent()
    }

    override fun feedback() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.type = "text/plain"
        emailIntent.data = Uri.parse("mailto:support@appsbystudio.co.uk")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "GeoShare Feedback")

        view.feedbackIntent(emailIntent)
    }

    override fun logout() {
        if (FirebaseAuth.getInstance() != null) {
            interactor.removeToken()
            FirebaseAuth.getInstance().signOut()
        } else {
            view.showErrorSnackbar("Could not log out!")
        }
    }

    override fun auth() {
        view.logoutIntent()
    }

    override fun clearSharedPreferences() {
        settingsPreferencesHelper.clear()
        trackingPreferencesHelper.clear()
        markerPreferencesHelper.clear()
    }

    override fun navDrawerState(open: Boolean) {
        if (open) {
            view.openNavDrawer()
        } else {
            view.closeNavDrawer()
        }
    }

    override fun stop() {
        interactor.removeAllListeners()
    }

    override fun friendAdded(key: String?, name: String?) {
        view.updateFriendsList(key, name)
    }

    override fun friendRemoved(key: String?) {
        view.removeFromFriendList(key)
    }

    override fun trackingAdded(key: String?, trackingState: Boolean?) {
        view.updateTrackingState(key, trackingState)
    }

    override fun trackingRemoved(key: String?) {
        view.removeTrackingState(key)
    }

    override fun profileUpdated() {
        view.updateFriendsList()
    }

    override fun error(error: String) {
        view.showError(error)
    }
}