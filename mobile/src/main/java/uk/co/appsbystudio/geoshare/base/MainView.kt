package uk.co.appsbystudio.geoshare.base

import android.support.v4.app.Fragment

interface MainView {

    fun updateFriendsList(uid: String?, name: String?)

    fun removeFromFriendList(uid: String?)

    fun updateTrackingState(uid: String?, trackingState: Boolean?)

    fun removeTrackingState(uid: String?)

    fun swapFragment(fragment: Fragment)

    fun friendsIntent()

    fun settingsIntent()

    fun logoutIntent()

    fun feedbackIntent()

    fun openNavDrawer()

    fun openFriendsNavDrawer()

    fun closeNavDrawer()

    fun closeFriendsNavDrawer()

    fun showError(message: String)

    fun showErrorSnackbar(message: String)

}