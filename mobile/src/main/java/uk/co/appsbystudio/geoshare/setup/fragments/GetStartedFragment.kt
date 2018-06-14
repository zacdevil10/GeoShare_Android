package uk.co.appsbystudio.geoshare.setup.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_get_started.*

import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.setup.InitialSetupActivity
import uk.co.appsbystudio.geoshare.setup.InitialSetupView

class GetStartedFragment : Fragment() {

    private var fragmentCallback: InitialSetupView? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            fragmentCallback = context as InitialSetupActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement AuthView")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_get_started, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_start_setup.setOnClickListener {
            fragmentCallback?.onNext()
        }
    }
}
