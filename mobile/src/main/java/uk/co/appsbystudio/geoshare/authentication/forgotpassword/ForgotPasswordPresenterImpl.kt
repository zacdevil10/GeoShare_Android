package uk.co.appsbystudio.geoshare.authentication.forgotpassword

class ForgotPasswordPresenterImpl(private val view: ForgotPasswordView, private val interactor: ForgotPasswordInteractor):
        ForgotPasswordPresenter, ForgotPasswordInteractor.OnRecoverFinishedListener {

    override fun validate(email: String) {
        view.showProgress()

        interactor.recover(email, this)
    }

    override fun onEmailError() {
        view.setEmailError()
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