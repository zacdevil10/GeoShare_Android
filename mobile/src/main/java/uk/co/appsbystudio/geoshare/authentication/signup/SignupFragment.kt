package uk.co.appsbystudio.geoshare.authentication.signup

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_signup.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.authentication.AuthActivity
import uk.co.appsbystudio.geoshare.authentication.AuthView

class SignupFragment : Fragment(), SignupView {

    private var fragmentCallback: AuthView? = null
    private var presenter: SignupPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        presenter = SignupPresenterImpl(this, SignupInteractorImpl())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress_button_signup.setOnClickListener {
            presenter?.validate(edit_name_signup.text.toString(), edit_email_signup.text.toString(), edit_password_signup.text.toString(), checkbox_terms_signup.isChecked)
        }

        text_terms_link_signup.setOnClickListener {
            presenter?.onTermsClick()
        }

        button_back_signup.setOnClickListener {
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

    override fun setNameError() {
        edit_name_signup.error = resources.getString(R.string.error_field_required)
        edit_name_signup.requestFocus()
    }

    override fun setEmailError() {
        edit_email_signup.error = resources.getString(R.string.error_field_required)
        edit_email_signup.requestFocus()
    }

    override fun setPasswordError() {
        edit_password_signup.error = resources.getString(R.string.error_field_required)
        edit_password_signup.requestFocus()
    }

    override fun setTermsError() {
        checkbox_terms_signup.error = resources.getString(R.string.error_field_required)
    }

    override fun showTerms() {
        //TODO: Create a dialog to show the terms
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://geoshare.appsbystudio.co.uk/terms")))
    }

    override fun showProgress() {
        progress_button_signup.startAnimation()
    }

    override fun hideProgress() {
        progress_button_signup.revertAnimation()
    }

    override fun updateUI() {
        fragmentCallback?.onSuccess()
    }

    override fun showError(error: String) {
        Toast.makeText(this.context, error, Toast.LENGTH_SHORT).show()
    }
}
