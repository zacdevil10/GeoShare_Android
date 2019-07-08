package uk.co.appsbystudio.geoshare.authentication.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_login.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.authentication.AuthActivity
import uk.co.appsbystudio.geoshare.authentication.AuthView
import uk.co.appsbystudio.geoshare.authentication.forgotpassword.ForgotPasswordFragment
import uk.co.appsbystudio.geoshare.authentication.signup.SignupFragment


class LoginFragment : Fragment(), LoginView {

    private var fragmentCallback: AuthView? = null
    private var presenter: LoginPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        presenter = LoginPresenterImpl(this, LoginInteractorImpl())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_button_login.setOnClickListener {
            presenter?.validate(edit_email_login.text.toString(), edit_password_login.text.toString())
        }

        button_signup_login.setOnClickListener {
            fragmentCallback?.setFragment(SignupFragment())
        }

        button_forgot_password_login.setOnClickListener {
            fragmentCallback?.setFragment(ForgotPasswordFragment())
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            fragmentCallback = context as AuthActivity
        } catch (e: ClassCastException) {
            throw ClassCastException("""${activity.toString()}must implement AuthView""")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.clearFindViewByIdCache()
    }

    override fun setEmailError() {
        edit_email_login.error = resources.getString(R.string.error_field_required)
        edit_email_login.requestFocus()
    }

    override fun setPasswordError() {
        edit_password_login.error = resources.getString(R.string.error_field_required)
        edit_password_login.requestFocus()
    }

    override fun showProgress() {
        progress_button_login.startAnimation()
    }

    override fun hideProgress() {
        progress_button_login.revertAnimation()
    }

    override fun showError(error: String) {
        Toast.makeText(this.context, error, Toast.LENGTH_SHORT).show()
    }

    override fun updateUI() {
        fragmentCallback?.onSuccess()
    }
}
