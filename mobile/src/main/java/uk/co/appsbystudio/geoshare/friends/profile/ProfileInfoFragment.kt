package uk.co.appsbystudio.geoshare.friends.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uk.co.appsbystudio.geoshare.Application
import uk.co.appsbystudio.geoshare.MainActivity
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.StringUtils.ellipsize
import uk.co.appsbystudio.geoshare.utils.TimeUtils.convertDate
import uk.co.appsbystudio.geoshare.utils.dialog.ShareOptions
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.geocoder.GeocodingFromLatLngTask

class ProfileInfoFragment : Fragment() {

    var uid: String? = null

    private var sharedPreferences: SharedPreferences? = null

    private var auth: FirebaseAuth? = null
    private var ref: DatabaseReference? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_info, container, false)

        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        ref = database.reference

        sharedPreferences = Application.getContext().getSharedPreferences("tracking", Context.MODE_PRIVATE)

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

        view.findViewById<ConstraintLayout>(R.id.profile_request_location).setOnClickListener({
            ref?.child("update")?.child(auth?.currentUser?.uid)?.child(uid)?.child("request_location")?.setValue(System.currentTimeMillis())
        })

        view.findViewById<ConstraintLayout>(R.id.profile_delete_location).setOnClickListener({
            ref?.child("current_location")?.child(auth?.currentUser?.uid)?.child(uid)?.removeValue()
        })

        if (sharedPreferences!!.getBoolean(uid, false)) {
            view.findViewById<TextView>(R.id.profile_share_location_label).text = "Stop sharing"
        } else {
            view.findViewById<TextView>(R.id.profile_share_location_label).text = "Share current location"
        }

        view.findViewById<ConstraintLayout>(R.id.profile_share_location).setOnClickListener({
            if (sharedPreferences!!.getBoolean(uid, false)) {
                stopSharing()
            } else {
                showSendLocationDialog()
            }
        })

        view.findViewById<ConstraintLayout>(R.id.profile_home_location).setOnClickListener({

        })

        view.findViewById<ConstraintLayout>(R.id.profile_work_location).setOnClickListener({

        })

        return view
    }

    private fun showSendLocationDialog() {
        val arguments = Bundle()
        arguments.putString("name", MainActivity.friendNames[uid])
        arguments.putString("friendId", uid)
        arguments.putString("uid", auth?.currentUser?.uid)

        val fragmentManager = activity?.fragmentManager
        val friendDialog = ShareOptions()
        friendDialog.arguments = arguments
        friendDialog.show(fragmentManager, "location_dialog")
    }

    private fun stopSharing() {
        ref?.child(FirebaseHelper.TRACKING)?.child(uid)?.child("tracking")?.child(auth?.currentUser?.uid)?.removeValue()
                ?.addOnSuccessListener(OnSuccessListener<Void> {
                    sharedPreferences?.edit()?.putBoolean(uid, false)?.apply()
                })
                ?.addOnFailureListener(OnFailureListener {
                    //TODO: Show a message (with "try again?" ?)
                })
    }
}
