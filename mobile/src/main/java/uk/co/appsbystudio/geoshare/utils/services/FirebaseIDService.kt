package uk.co.appsbystudio.geoshare.utils.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class FirebaseIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token

        sendRegistrationToServer(refreshedToken)
    }

    private fun sendRegistrationToServer(token: String?) {
        //Add to secure part of firebase database
        val databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseHelper.TOKEN)
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && token != null) {
            databaseReference.child(user.uid).child(token).child("platform").setValue("android")
        }
    }
}
