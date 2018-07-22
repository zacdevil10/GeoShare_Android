package uk.co.appsbystudio.geoshare.authentication.login

interface LoginPresenter {

    fun validate(email: String, password: String)
}