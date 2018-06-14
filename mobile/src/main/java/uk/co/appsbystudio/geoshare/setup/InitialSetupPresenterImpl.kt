package uk.co.appsbystudio.geoshare.setup

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class InitialSetupPresenterImpl(private val view: InitialSetupView): InitialSetupPresenter {

    override fun addDeviceToken() {
        val user = FirebaseAuth.getInstance().currentUser
        val token = FirebaseInstanceId.getInstance().token

        if (user != null && token != null) {
            FirebaseDatabase.getInstance().getReference(FirebaseHelper.TOKEN)
                    .child("${user.uid}/$token/platform")
                    .setValue("android")
        }
    }

    override fun onPermissionsResult() {
        view.onNext()
    }

    override fun onError(error: String) {
        view.showToast(error)
    }
}