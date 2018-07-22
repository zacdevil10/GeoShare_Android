package uk.co.appsbystudio.geoshare.authentication.signup

interface SignupInteractor {

    interface OnSignupFinishedListener {
        fun onNameError()

        fun onEmailError()

        fun onPasswordError()

        fun onTermsError()

        fun onSuccess()

        fun onFailure(error: String)
    }

    fun signup(name: String, email: String, password: String, terms: Boolean, listener: OnSignupFinishedListener)

}