package uk.co.appsbystudio.geoshare.base

import android.app.DialogFragment
import android.content.Intent
import android.support.v4.app.Fragment

interface MainView {

    fun updateFriendsList(uid: String?, name: String?)

    fun removeFromFriendList(uid: String?)

    fun updateTrackingState(uid: String?, trackingState: Boolean?)

    fun removeTrackingState(uid: String?)

    fun showFragment(fragment: Fragment)

    fun friendsIntent()

    fun settingsIntent()

    fun logoutIntent()

    fun feedbackIntent(intent: Intent)

    fun startTrackingServiceIntent()

    fun stopTrackingServiceIntent()

    fun showDialog(dialog: DialogFragment, tag: String)

    fun setDisplayName(name: String?)

    fun updateProfilePicture()

    fun markerToggleState(state: Boolean?)

    fun openNavDrawer()

    fun openFriendsNavDrawer()

    fun closeNavDrawer()

    fun closeFriendsNavDrawer()

    fun showError(message: String)

    fun showErrorSnackbar(message: String)

}