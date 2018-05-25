package uk.co.appsbystudio.geoshare.authentication

class AuthPresenterImpl(private val authView: AuthView) : AuthPresenter {

    override fun updateUI() {
        authView.onSuccess()
    }

}