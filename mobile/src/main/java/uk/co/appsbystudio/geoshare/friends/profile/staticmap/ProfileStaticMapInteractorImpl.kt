package uk.co.appsbystudio.geoshare.friends.profile.staticmap

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class ProfileStaticMapInteractorImpl: ProfileStaticMapInteractor {

    override fun getLocation(uid: String, listener: ProfileStaticMapInteractor.OnFirebaseListener) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val location = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val location = dataSnapshot.getValue(DatabaseLocations::class.java)
                    if (location != null) listener.setImage(location)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    listener.error(databaseError.message)
                }
            }

            FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.CURRENT_LOCATION}/${user.uid}/$uid").addListenerForSingleValueEvent(location)
        }
    }
}