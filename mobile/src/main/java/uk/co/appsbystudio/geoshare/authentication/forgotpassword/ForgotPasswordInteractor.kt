package uk.co.appsbystudio.geoshare.authentication.forgotpassword

interface ForgotPasswordInteractor {

    interface OnRecoverFinishedListener {
        fun onEmailError()

        fun onSuccess()

        fun onFailure(error: String)
    }

    fun recover(email: String, listener: OnRecoverFinishedListener)

}