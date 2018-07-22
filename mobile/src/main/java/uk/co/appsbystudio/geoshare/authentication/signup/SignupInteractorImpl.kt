package uk.co.appsbystudio.geoshare.authentication.signup

import android.text.TextUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.UserInformation

class SignupInteractorImpl: SignupInteractor {

    override fun signup(name: String, email: String, password: String, terms: Boolean, listener: SignupInteractor.OnSignupFinishedListener) {
        if (TextUtils.isEmpty(name)) {
            listener.onNameError()
            return
        }
        if (TextUtils.isEmpty(email)) {
            listener.onEmailError()
            return
        }
        if (TextUtils.isEmpty(password)) {
            listener.onPasswordError()
            return
        }

        if (!terms) {
            listener.onTermsError()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { it: Task<AuthResult> ->
            if (it.isSuccessful) {
                val profileChangeRequest: UserProfileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                val user = FirebaseAuth.getInstance().currentUser
                val userInformation = UserInformation(name, name.toLowerCase())

                if (user != null) {
                    user.updateProfile(profileChangeRequest)
                    FirebaseDatabase.getInstance().reference.child(FirebaseHelper.USERS).child(user.uid).setValue(userInformation)
                }

                listener.onSuccess()
            } else {
                listener.onFailure(it.exception?.message.toString())
            }
        }
    }
}