package uk.co.appsbystudio.geoshare.authentication.forgotpassword

import android.text.TextUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordInteractorImpl: ForgotPasswordInteractor {

    override fun recover(email: String, listener: ForgotPasswordInteractor.OnRecoverFinishedListener) {
        if (TextUtils.isEmpty(email)) {
            listener.onEmailError()
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { it: Task<Void> ->
            if (it.isSuccessful) {
                listener.onSuccess()
            } else {
                listener.onFailure(it.exception?.message.toString())
            }
        }
    }

}