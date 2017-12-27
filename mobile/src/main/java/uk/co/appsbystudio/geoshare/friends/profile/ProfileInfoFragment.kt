package uk.co.appsbystudio.geoshare.friends.profile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.StringUtils.ellipsize
import uk.co.appsbystudio.geoshare.utils.TimeUtils.convertDate
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.geocoder.GeocodingFromLatLngTask

class ProfileInfoFragment : Fragment() {

    var uid: String? = null

    private var ref: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_info, container, false)

        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        ref = database.reference

        uid = arguments?.getString("uid")

        val singleLocation = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val location = dataSnapshot.getValue(DatabaseLocations::class.java)
                if (location != null) {
                    view.findViewById<TextView>(R.id.location_text).text = ellipsize(GeocodingFromLatLngTask(location.lat, location.longitude).execute().get(), 46)
                    view.findViewById<TextView>(R.id.location_timestamp_text).text = convertDate(location.timestamp)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        ref!!.child(FirebaseHelper.CURRENT_LOCATION).child(auth?.currentUser?.uid).child(uid).addListenerForSingleValueEvent(singleLocation)

        return view
    }
}
