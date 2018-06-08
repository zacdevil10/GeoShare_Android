package uk.co.appsbystudio.geoshare.friends.profile.info

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uk.co.appsbystudio.geoshare.Application
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.convertDate
import uk.co.appsbystudio.geoshare.utils.dialog.ShareOptions
import uk.co.appsbystudio.geoshare.utils.ellipsize
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.geocoder.GeocodingFromLatLngTask

class ProfileInfoFragment : Fragment() {

    var uid: String? = null

    private var sharedPreferences: SharedPreferences? = null

    private var auth: FirebaseAuth? = null
    private var ref: DatabaseReference? = null

    private var profileLocationLabel: TextView? = null
    private var profileLocationTimestampLabel: TextView? = null
    private var shareLocationLabel: TextView? = null

    private var profileDeleteLocationLayout: ConstraintLayout? = null

    companion object {
        fun newInstance(uid: String?) = ProfileInfoFragment().apply {
            arguments = Bundle().apply {
                putString("uid", uid)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_info, container, false)

        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        ref = database.reference

        sharedPreferences = Application.getContext().getSharedPreferences("tracking", Context.MODE_PRIVATE)

        uid = arguments?.getString("uid")

        profileLocationLabel = view.findViewById(R.id.location_text)
        profileLocationTimestampLabel = view.findViewById(R.id.location_timestamp_text)
        shareLocationLabel = view.findViewById(R.id.profile_share_location_label)

        profileDeleteLocationLayout = view.findViewById(R.id.profile_delete_location)

        val singleLocation = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val location = dataSnapshot.getValue(DatabaseLocations::class.java)
                if (location != null) {
                    profileLocationLabel?.text = GeocodingFromLatLngTask(location.lat, location.longitude).execute().get().ellipsize(43)
                    profileLocationTimestampLabel?.text = location.timestamp.convertDate()

                    setDeleteButton()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }

        ref!!.child(FirebaseHelper.CURRENT_LOCATION).child(auth!!.currentUser!!.uid).child(uid!!).addListenerForSingleValueEvent(singleLocation)

        view.findViewById<ConstraintLayout>(R.id.profile_request_location).setOnClickListener({
            ref?.child("update")?.child(auth?.currentUser!!.uid)?.child(uid!!)?.child("request_location")?.setValue(System.currentTimeMillis())
        })

        if (sharedPreferences!!.getBoolean(uid, false)) {
            shareLocationLabel?.text = "Stop sharing"
        } else {
            shareLocationLabel?.text = "Share current location"
        }

        view.findViewById<ConstraintLayout>(R.id.profile_share_location).setOnClickListener({
            if (sharedPreferences!!.getBoolean(uid, false)) {
                stopSharing()
            } else {
                showSendLocationDialog()
            }
        })

        /*view.findViewById<ConstraintLayout>(R.id.profile_home_location).setOnClickListener({

        })

        view.findViewById<ConstraintLayout>(R.id.profile_work_location).setOnClickListener({

        })*/

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
        ref?.child(FirebaseHelper.TRACKING)?.child(uid!!)?.child("tracking")?.child(auth?.currentUser!!.uid)?.removeValue()
                ?.addOnSuccessListener({
                    sharedPreferences?.edit()?.putBoolean(uid, false)?.apply()
                    shareLocationLabel?.text = "Share current location"

                })
                ?.addOnFailureListener({
                    //TODO: Show a message (with "try again?" ?)
                })
    }

    /*private fun stopTracking() {
        ref?.child(FirebaseHelper.TRACKING)?.child("")
    }*/

    private fun setDeleteButton() {
        profileDeleteLocationLayout?.visibility = View.VISIBLE

        profileDeleteLocationLayout?.setOnClickListener({
            ref?.child("current_location")?.child(auth?.currentUser!!.uid)?.child(uid!!)?.removeValue()
                    ?.addOnSuccessListener({
                        profileLocationLabel?.text = "No location"
                        profileLocationTimestampLabel?.text = "Never"
                        profileDeleteLocationLayout?.visibility = View.GONE
                    })
        })
    }
}
