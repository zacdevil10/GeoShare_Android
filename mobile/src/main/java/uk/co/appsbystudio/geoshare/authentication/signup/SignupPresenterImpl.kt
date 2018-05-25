package uk.co.appsbystudio.geoshare.authentication.signup

class SignupPresenterImpl(private val signupView: SignupView, private val signupInteractor: SignupInteractor): SignupPresenter, SignupInteractor.OnSignupFinishedListener {

    override fun validate(name: String, email: String, password: String, terms: Boolean) {
        signupView.showProgress()

        signupInteractor.signup(name, email, password, terms, this)
    }

    override fun onTermsClick() {
        signupView.showTerms()
    }

    override fun onNameError() {
        signupView.setNameError()
        signupView.hideProgress()
    }

    override fun onEmailError() {
        signupView.setEmailError()
        signupView.hideProgress()
    }

    override fun onPasswordError() {
        signupView.setPasswordError()
        signupView.hideProgress()
    }

    override fun onTermsError() {
        signupView.setTermsError()
        signupView.hideProgress()
    }

    override fun onSuccess() {
        signupView.updateUI()
    }

    override fun onFailure(error: String) {
        signupView.hideProgress()
        signupView.showError(error)
    }
}