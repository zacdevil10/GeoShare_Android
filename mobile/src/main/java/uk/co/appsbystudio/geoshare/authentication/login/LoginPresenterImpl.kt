package uk.co.appsbystudio.geoshare.authentication.login

class LoginPresenterImpl(private val view: LoginView, private val interactor: LoginInteractor): LoginPresenter, LoginInteractor.OnLoginFinishedListener {

    override fun validate(email: String, password: String) {
        view.showProgress()

        interactor.login(email, password, this)
    }

    override fun onEmailError() {
        view.setEmailError()
        view.hideProgress()
    }

    override fun onPasswordError() {
        view.setPasswordError()
        view.hideProgress()
    }

    override fun onSuccess() {
        view.updateUI()
    }

    override fun onFailure(error: String) {
        view.hideProgress()
        view.showError(error)
    }

}