package uk.co.appsbystudio.geoshare.authentication.forgotpassword

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_forgot_password.*

import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.authentication.AuthActivity
import uk.co.appsbystudio.geoshare.authentication.AuthView

class ForgotPasswordFragment : Fragment(), ForgotPasswordView {

    private var fragmentCallback: AuthView? = null
    private var forgotPasswordPresenter: ForgotPasswordPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        forgotPasswordPresenter = ForgotPasswordPresenterImpl(this, ForgotPasswordInteractorImpl())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_button_forgot_password.setOnClickListener {
            forgotPasswordPresenter?.validate(edit_email_forgot_password.text.toString())
        }

        button_back_forgot_password.setOnClickListener {
            fragmentCallback?.onBack()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            fragmentCallback = context as AuthActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement AuthView")
        }
    }

    override fun setEmailError() {
        edit_email_forgot_password.error = resources.getString(R.string.error_field_required)
    }

    override fun showProgress() {
        progress_button_forgot_password.startAnimation()
    }

    override fun hideProgress() {
        progress_button_forgot_password.revertAnimation()
    }

    override fun updateUI() {
        //TODO: Replace string
        Toast.makeText(this.context, "Email sent!", Toast.LENGTH_SHORT).show()
        fragmentCallback?.onBack()
    }

    override fun showError(error: String) {
        Toast.makeText(this.context, error, Toast.LENGTH_SHORT).show()
    }
}
