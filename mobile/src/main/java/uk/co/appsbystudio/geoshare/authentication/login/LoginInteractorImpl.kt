package uk.co.appsbystudio.geoshare.authentication.login

import android.text.TextUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginInteractorImpl: LoginInteractor {

    override fun login(email: String, password: String, listener: LoginInteractor.OnLoginFinishedListener) {
        if (TextUtils.isEmpty(email)) {
            listener.onEmailError()
            return
        }
        if (TextUtils.isEmpty(password)) {
            listener.onPasswordError()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { it: Task<AuthResult> ->
                    if (it.isSuccessful) {
                        listener.onSuccess()
                    } else {
                        listener.onFailure(it.exception?.message.toString())
                    }
                }
    }

}