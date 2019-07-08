package uk.co.appsbystudio.geoshare.setup.fragments.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_setup_profile.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.setup.InitialSetupActivity
import uk.co.appsbystudio.geoshare.setup.InitialSetupView

class SetupProfileFragment : Fragment(), SetupProfileView {

    private var fragmentCallback: InitialSetupView? = null
    private var presenter: SetupProfilePresenter? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            fragmentCallback = context as InitialSetupActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement InitialSetupView")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = SetupProfilePresenterImpl(this)

        return inflater.inflate(R.layout.fragment_setup_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.imageAvailable()

        button_set_picture_profile.setOnClickListener {
            fragmentCallback?.onShowProfileDialog()
        }

        button_back_profile.setOnClickListener {
            fragmentCallback?.onBack()
        }

        button_next_profile.setOnClickListener {
            fragmentCallback?.onNext()
        }
    }

    override fun updateUIText() {
        descriptionProfile.setText(R.string.desc_picture_is_set)
        button_set_picture_profile.setText(R.string.upload_new_picture)
    }
}
