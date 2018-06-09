package uk.co.appsbystudio.geoshare.friends.profile.staticmap

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile_static_map.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.DownloadImageTask
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class ProfileStaticMapFragment : Fragment() {

    lateinit var uid: String

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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val singleLocation = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val location = dataSnapshot.getValue(DatabaseLocations::class.java)
                if (location != null) {
                    DownloadImageTask("""https://maps.googleapis.com/maps/api/staticmap?center=${location.lat},${location.longitude}&zoom=17&size=${static_map.height}x${static_map.width}&markers=color:red%7C${location.lat},${location.longitude}&key=AIzaSyB7fJe5C8nfedKovcp_oLe7hrYm9bRgMlU""",
                            static_map).execute()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseDatabase.getInstance().reference.child(FirebaseHelper.CURRENT_LOCATION).child(FirebaseAuth.getInstance().currentUser?.uid!!).child(uid).addListenerForSingleValueEvent(singleLocation)
        }
    }

}
