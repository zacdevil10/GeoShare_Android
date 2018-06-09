package uk.co.appsbystudio.geoshare.friends.profile.staticmap

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile_static_map.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.DownloadImageTask
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class ProfileStaticMapFragment : Fragment(), ProfileStaticMapView {

    lateinit var uid: String
    private var profileStaticMapPresenter: ProfileStaticMapPresenter? = null

    companion object {
        fun newInstance(uid: String?) = ProfileStaticMapFragment().apply {
            arguments = Bundle().apply {
                putString("uid", uid)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_static_map, container, false)

        uid = arguments?.getString("uid").toString()

        profileStaticMapPresenter = ProfileStaticMapPresenterImpl(this, ProfileStaticMapInteractorImpl())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileStaticMapPresenter?.location(uid)
    }

    override fun setMapImage(bitmap: Bitmap) {
        image_map_profile.setImageBitmap(bitmap)
    }

    override fun showError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

}