package uk.co.appsbystudio.geoshare.authentication.signup

class SignupPresenterImpl(private val view: SignupView, private val interactor: SignupInteractor): SignupPresenter, SignupInteractor.OnSignupFinishedListener {

    override fun validate(name: String, email: String, password: String, terms: Boolean) {
        view.showProgress()

        interactor.signup(name, email, password, terms, this)
    }

    override fun onTermsClick() {
        view.showTerms()
    }

    override fun onNameError() {
        view.setNameError()
        view.hideProgress()
    }

    override fun onEmailError() {
        view.setEmailError()
        view.hideProgress()
    }

    override fun onPasswordError() {
        view.setPasswordError()
        view.hideProgress()
    }

    override fun onTermsError() {
        view.setTermsError()
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