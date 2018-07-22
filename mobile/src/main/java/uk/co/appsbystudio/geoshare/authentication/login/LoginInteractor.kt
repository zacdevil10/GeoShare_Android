package uk.co.appsbystudio.geoshare.authentication.login

interface LoginInteractor {

    interface OnLoginFinishedListener {
        fun onEmailError()

        fun onPasswordError()

        fun onSuccess()

        fun onFailure(error: String)
    }

    fun login(email: String, password: String, listener: OnLoginFinishedListener)
}