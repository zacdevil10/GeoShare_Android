package uk.co.appsbystudio.geoshare.base

import android.app.DialogFragment
import androidx.fragment.app.Fragment
import de.hdodenhof.circleimageview.CircleImageView

interface MainPresenter {

    fun getFriends()

    fun getFriendsTrackingState()

    fun updatedProfileListener()

    fun setTrackingService()

    fun stopTrackingService()

    fun setFriendSharingState(uid: String, state: Boolean)

    fun updateNavProfilePicture(view: CircleImageView?, storageDirectory: String)

    fun updateNavDisplayName()

    fun setMarkerVisibilityState()

    fun swapFragment(fragment: Fragment)

    fun openDialog(dialog: DialogFragment, tag: String)

    fun friends()

    fun settings()

    fun feedback()

    fun logout()

    fun auth()

    fun clearSharedPreferences()

    fun navDrawerState(open: Boolean)

    fun stop()

}