package uk.co.appsbystudio.geoshare.authentication.login

interface LoginView {

    fun setEmailError()

    fun setPasswordError()

    fun showProgress()

    fun hideProgress()

    fun updateUI()

    fun showError(error: String)

}
