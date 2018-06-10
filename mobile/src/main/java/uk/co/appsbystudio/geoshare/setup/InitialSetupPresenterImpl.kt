package uk.co.appsbystudio.geoshare.setup

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class InitialSetupPresenterImpl(private val view: InitialSetupView): InitialSetupPresenter {

    override fun addDeviceToken() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        if (auth.currentUser != null && FirebaseInstanceId.getInstance().token != null) {
            FirebaseDatabase.getInstance().getReference(FirebaseHelper.TOKEN)
                    .child(auth.currentUser?.uid!!)
                    .child(FirebaseInstanceId.getInstance().token!!)
                    .child("platform")
                    .setValue("android")
        }
    }

    override fun onPermissionsResult() {
        view.onNext()
    }

    override fun onError(error: String) {
        view.onError(error)
    }
}