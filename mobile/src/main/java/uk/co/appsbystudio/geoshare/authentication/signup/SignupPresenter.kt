package uk.co.appsbystudio.geoshare.authentication.signup

interface SignupPresenter {

    fun validate(name: String, email: String, password: String, terms: Boolean)

    fun onTermsClick()

}