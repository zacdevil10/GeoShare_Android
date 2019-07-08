package uk.co.appsbystudio.geoshare.friends.profile.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile_info.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.manager.FriendsManager
import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.dialog.ShareOptions

class ProfileInfoFragment : Fragment(), ProfileInfoView {

    lateinit var uid: String

    private var presenter: ProfileInfoPresenter? = null

    companion object {
        fun newInstance(uid: String?) = ProfileInfoFragment().apply {
            arguments = Bundle().apply {
                putString("uid", uid)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_info, container, false)

        uid = arguments?.getString("uid").toString()

        presenter = ProfileInfoPresenterImpl(this,
                TrackingPreferencesHelper(context?.getSharedPreferences("tracking", Context.MODE_PRIVATE)),
                ProfileInfoInteractorImpl())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.updateCurrentLocation(uid)
        presenter?.updateTrackingState(uid)

        constraint_request_location_profile.setOnClickListener {
            presenter?.requestLocation(uid)
        }

        constraint_share_location_profile.setOnClickListener {
            presenter?.shareLocation(uid)
        }

        constraint_delete_location_profile.setOnClickListener {
            presenter?.removeLocation(uid)
        }
    }

    override fun setShareText(string: String) {
        text_share_location_profile?.text = string
    }

    override fun setLocationItemText(address: LiveData<String>?, timestamp: String?) {
        text_timestamp_profile?.text = timestamp

        val observer = Observer<String> { t ->
            text_location_profile.text = t
        }

        if (address == null) {
            text_location_profile.text = getString(R.string.no_location)
        }

        address?.observe(this, observer)
    }

    override fun showShareDialog() {
        val arguments = Bundle()
        arguments.putString("name", FriendsManager.friendsMap[uid])
        arguments.putString("friendId", uid)
        arguments.putString("uid", FirebaseAuth.getInstance()?.currentUser?.uid)

        val fragmentManager = activity?.fragmentManager
        val friendDialog = ShareOptions()
        friendDialog.arguments = arguments
        friendDialog.show(fragmentManager, "location_dialog")
    }

    override fun deleteButtonVisibility(visible: Int) {
        constraint_delete_location_profile?.visibility = visible
    }

    override fun showToast(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }
}