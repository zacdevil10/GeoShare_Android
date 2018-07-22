package uk.co.appsbystudio.geoshare.authentication.forgotpassword

interface ForgotPasswordView {

    fun setEmailError()

    fun showProgress()

    fun hideProgress()

    fun updateUI()

    fun showError(error: String)

}