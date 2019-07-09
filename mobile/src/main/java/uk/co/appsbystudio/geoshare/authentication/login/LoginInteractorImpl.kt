package uk.co.appsbystudio.geoshare.authentication.login

import com.google.firebase.auth.FirebaseAuth

class LoginInteractorImpl: LoginInteractor {

    override fun login(email: String, password: String, listener: LoginInteractor.OnLoginFinishedListener) {
        if (email.isEmpty()) {
            listener.onEmailError()
            return
        }
        if (password.isEmpty()) {
            listener.onPasswordError()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        listener.onSuccess()
                    } else {
                        listener.onFailure(it.exception?.message.toString())
                    }
                }
    }
}