package uk.co.appsbystudio.geoshare.authentication.forgotpassword

import android.text.TextUtils
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordInteractorImpl: ForgotPasswordInteractor {

    override fun recover(email: String, listener: ForgotPasswordInteractor.OnRecoverFinishedListener) {
        if (TextUtils.isEmpty(email)) {
            listener.onEmailError()
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                listener.onSuccess()
            } else {
                listener.onFailure(it.exception?.message.toString())
            }
        }
    }

}