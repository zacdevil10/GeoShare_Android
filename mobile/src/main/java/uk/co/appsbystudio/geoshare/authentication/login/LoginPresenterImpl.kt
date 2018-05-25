package uk.co.appsbystudio.geoshare.authentication.login

class LoginPresenterImpl(private val loginView: LoginView, private val loginInteractor: LoginInteractor): LoginPresenter, LoginInteractor.OnLoginFinishedListener {

    override fun validate(email: String, password: String) {
        loginView.showProgress()

        loginInteractor.login(email, password, this)
    }

    override fun onEmailError() {
        loginView.setEmailError()
        loginView.hideProgress()
    }

    override fun onPasswordError() {
        loginView.setPasswordError()
        loginView.hideProgress()
    }

    override fun onSuccess() {
        loginView.updateUI()
    }

    override fun onFailure(error: String) {
        loginView.hideProgress()
        loginView.showError(error)
    }

}