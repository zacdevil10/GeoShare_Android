package uk.co.appsbystudio.geoshare.friends.profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import uk.co.appsbystudio.geoshare.R


/**
 * A simple [Fragment] subclass.
 */
class ProfileInfoFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_info, container, false)
    }

}// Required empty public constructor
