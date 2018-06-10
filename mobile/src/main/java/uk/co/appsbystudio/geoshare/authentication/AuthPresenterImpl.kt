package uk.co.appsbystudio.geoshare.authentication

class AuthPresenterImpl(private val view: AuthView) : AuthPresenter {

    override fun updateUI() {
        view.onSuccess()
    }

}