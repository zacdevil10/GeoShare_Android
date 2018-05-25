package uk.co.appsbystudio.geoshare.friends.profile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.DownloadImageTask
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class ProfileStaticMapFragment : Fragment() {

    var uid: String? = null

    private var ref: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_static_map, container, false)

        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        ref = database.reference

        uid = arguments?.getString("uid")

        val staticMapImageView: ImageView = view.findViewById(R.id.static_map)

        val singleLocation = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val location = dataSnapshot.getValue(DatabaseLocations::class.java)
                if (location != null) {
                    DownloadImageTask("https://maps.googleapis.com/maps/api/staticmap?" +
                            "center=" + location.lat + "," + location.longitude +
                            "&zoom=17" +
                            "&size=500x500" +
                            "&markers=color:red%7C" + location.lat + "," + location.longitude +
                            "&key=AIzaSyB7fJe5C8nfedKovcp_oLe7hrYm9bRgMlU",
                            staticMapImageView).execute()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        ref!!.child(FirebaseHelper.CURRENT_LOCATION).child(auth.currentUser!!.uid).child(uid!!).addListenerForSingleValueEvent(singleLocation)

        return view
    }

}
