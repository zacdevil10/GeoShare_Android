package uk.co.appsbystudio.geoshare.authentication.signup

interface SignupView {

    fun setNameError()

    fun setEmailError()

    fun setPasswordError()

    fun setTermsError()

    fun showProgress()

    fun hideProgress()

    fun showTerms()

    fun updateUI()

    fun showError(error: String)

}