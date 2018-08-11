package uk.co.appsbystudio.geoshare.friends.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uk.co.appsbystudio.geoshare.utils.ShowMarkerPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class ProfilePresenterImpl(private val view: ProfileView, private val trackingPreferencesHelper: TrackingPreferencesHelper,
                           private val markerPreferencesHelper: ShowMarkerPreferencesHelper): ProfilePresenter {

    override fun removeFriendDialog(uid: String?) {
        view.showDialog(uid)
    }

    override fun removeFriend(uid: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && uid != null) {
            FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/${user.uid}/$uid").removeValue()
                    .addOnSuccessListener {
                        view.closeProfile()
                    }
                    .addOnFailureListener {
                        view.showError("Could not remove friend")
                    }
            if (trackingPreferencesHelper.exists(uid)!!) trackingPreferencesHelper.removeEntry(uid)
            if (markerPreferencesHelper.exists(uid)!!) markerPreferencesHelper.removeEntry(uid)
        }
    }

    override fun setViewPagerPosition(position: Int) {
        view.setPosition(position)
    }
}