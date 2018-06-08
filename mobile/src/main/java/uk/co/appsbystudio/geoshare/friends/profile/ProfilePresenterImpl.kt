package uk.co.appsbystudio.geoshare.friends.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uk.co.appsbystudio.geoshare.utils.ShowMarkerPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class ProfilePresenterImpl(private val profileView: ProfileView,
                           private val trackingPreferencesHelper: TrackingPreferencesHelper,
                           private val showMarkerPreferencesHelper: ShowMarkerPreferencesHelper): ProfilePresenter {

    override fun removeFriendDialog(uid: String?) {
        profileView.showDialog(uid)
    }

    override fun removeFriend(uid: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && uid != null) {
            FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/${user.uid}/$uid").removeValue()
                    .addOnSuccessListener({
                        profileView.closeProfile()
                    })
                    .addOnFailureListener({
                        profileView.showError("Could not remove friend")
                    })
            if (trackingPreferencesHelper.exists(uid)!!) trackingPreferencesHelper.removeEntry(uid)
            if (showMarkerPreferencesHelper.exists(uid)!!) showMarkerPreferencesHelper.removeEntry(uid)
        }
    }
}