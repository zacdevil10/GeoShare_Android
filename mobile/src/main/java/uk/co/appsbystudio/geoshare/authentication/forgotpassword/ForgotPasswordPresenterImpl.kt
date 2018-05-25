package uk.co.appsbystudio.geoshare.authentication.forgotpassword

class ForgotPasswordPresenterImpl(private val forgotPasswordView: ForgotPasswordView, private val forgotPasswordInteractor: ForgotPasswordInteractor):
        ForgotPasswordPresenter, ForgotPasswordInteractor.OnRecoverFinishedListener {

    override fun validate(email: String) {
        forgotPasswordView.showProgress()

        forgotPasswordInteractor.recover(email, this)
    }

    override fun onEmailError() {
        forgotPasswordView.setEmailError()
        forgotPasswordView.hideProgress()
    }

    override fun onSuccess() {
        forgotPasswordView.updateUI()
    }

    override fun onFailure(error: String) {
        forgotPasswordView.hideProgress()
        forgotPasswordView.showError(error)
    }
}