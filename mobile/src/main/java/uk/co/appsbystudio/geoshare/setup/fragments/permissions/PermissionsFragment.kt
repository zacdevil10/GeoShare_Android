package uk.co.appsbystudio.geoshare.setup.fragments.permissions

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_permissions.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.setup.InitialSetupActivity
import uk.co.appsbystudio.geoshare.setup.InitialSetupView

class PermissionsFragment : Fragment(), PermissionsView {

    private var fragmentCallback: InitialSetupView? = null
    private var presenter: PermissionsPresenter? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            fragmentCallback = context as InitialSetupActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement InitialSetupView")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_permissions, container, false)

        presenter = PermissionsPresenterImpl(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_allow_permissions.setOnClickListener {
            if (!fragmentCallback?.hasPermissions()!!) {
                fragmentCallback?.requestPermissions()
            } else {
                presenter?.onResult(getString(R.string.permissions_granted))
            }
        }

        button_next_permissions.setOnClickListener {
            fragmentCallback?.onNext()
        }
    }

    override fun onResult(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }
}
