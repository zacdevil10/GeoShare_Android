package uk.co.appsbystudio.geoshare.friends.profile.info

import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class ProfileInfoInteractorImpl: ProfileInfoInteractor {

    override fun getLocation(uid: String, listener: ProfileInfoInteractor.TrackingStateChangeListener) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val singleLocation = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val location = dataSnapshot.getValue(DatabaseLocations::class.java)
                    if (location != null) {
                        listener.updateLocationText(location)
                        listener.updateDeleteState(View.VISIBLE)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    listener.error(databaseError.message)
                }
            }

            FirebaseDatabase.getInstance().reference.child(FirebaseHelper.CURRENT_LOCATION).child(user.uid).child(uid).addListenerForSingleValueEvent(singleLocation)
        }
    }

    override fun deleteLocation(uid: String, listener: ProfileInfoInteractor.TrackingStateChangeListener) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.CURRENT_LOCATION}/${user.uid}/$uid").removeValue()
                    .addOnSuccessListener {
                        listener.resetLocationText()
                        listener.updateDeleteState(View.GONE)
                        listener.success("Location deleted")
                    }.addOnFailureListener {
                        listener.error(it.message.toString())
                    }
        }
    }

    override fun sendLocationRequest(uid: String, listener: ProfileInfoInteractor.TrackingStateChangeListener) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.UPDATE}/${user.uid}/$uid/${FirebaseHelper.REQUEST_LOCATION}").setValue(System.currentTimeMillis())
                    .addOnSuccessListener {
                        listener.success("Request sent")
                    }.addOnFailureListener {
                        listener.error(it.message.toString())
                    }
        }
    }

    override fun stopSharing(uid: String, listener: ProfileInfoInteractor.TrackingStateChangeListener) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.TRACKING}/$uid/${FirebaseHelper.TRACKING}/${user.uid}").removeValue()
                    .addOnSuccessListener {
                        listener.updateShareState(uid, "Share current location")
                    }.addOnFailureListener {
                        listener.error(it.message.toString())
                    }
        }
    }
}