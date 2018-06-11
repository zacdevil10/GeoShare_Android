package uk.co.appsbystudio.geoshare.utils.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.GPSTracking
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.TrackingInfo
import uk.co.appsbystudio.geoshare.utils.services.TrackingService
import java.text.SimpleDateFormat
import java.util.*

class ShareOptions : DialogFragment() {
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val args = arguments
        val name = args.getString("name")
        val friendId = args.getString("friendId")
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val gpsTracking = GPSTracking(activity)

        val trackingPreferences = activity.getSharedPreferences("tracking", Context.MODE_PRIVATE)
        val timerPreferences = activity.getSharedPreferences("tracking_time", Context.MODE_PRIVATE)

        val optionsMenu = AlertDialog.Builder(activity, R.style.DialogTheme)

        optionsMenu.setTitle("Share your location with $name?").setMultiChoiceItems(R.array.shareLocationOptions, null) { dialog, which, isChecked ->
            when (which) {
                0 -> if (!isChecked) (dialog as AlertDialog).listView.setItemChecked(1, false)
                1 -> if (isChecked) (dialog as AlertDialog).listView.setItemChecked(0, true)
            }
        }.setPositiveButton("Share", null).setNegativeButton("Cancel") { _, _ -> dismiss()}

        val dialog = optionsMenu.create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if ((dialog as AlertDialog).listView.checkedItemPositions.get(0) && !dialog.listView.checkedItemPositions.get(1)) {
                val lastShare = System.currentTimeMillis() - timerPreferences.getLong(friendId, 0)
                if (lastShare > 300000) { //5 mins in millis
                    val databaseLocations = DatabaseLocations(gpsTracking.getLongitude(), gpsTracking.getLatitude(), System.currentTimeMillis())

                    val databaseReference = FirebaseDatabase.getInstance().reference
                    if (uid != null && friendId != null) {
                        databaseReference.child("current_location").child(friendId).child(uid).setValue(databaseLocations)
                                .addOnSuccessListener {
                                    timerPreferences.edit().putLong(friendId, System.currentTimeMillis()).apply()
                                }.addOnFailureListener {
                                    Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
                                }
                        trackingPreferences.edit().putBoolean(friendId, false).apply()
                    }
                } else {
                    val timeLeft = (300000 - lastShare)
                    val format = SimpleDateFormat("mm:ss", Locale.getDefault())
                    Toast.makeText(activity, "Please wait another ${format.format(timeLeft)}", Toast.LENGTH_LONG).show()
                }
            } else if (dialog.listView.checkedItemPositions.get(0) && dialog.listView.checkedItemPositions.get(1)) {
                val databaseReference = FirebaseDatabase.getInstance().reference
                if (uid != null && friendId != null) {
                    val trackingInfo = TrackingInfo(true, System.currentTimeMillis())
                    databaseReference.child(FirebaseHelper.TRACKING).child(friendId).child("tracking").child(uid).setValue(trackingInfo)
                            .addOnSuccessListener {
                                trackingPreferences.edit().putBoolean(friendId, true).apply()
                                if (!TrackingService.isRunning) {
                                    val trackingService = Intent(activity, TrackingService::class.java)
                                    activity.startService(trackingService)
                                }
                            }.addOnFailureListener {
                                Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show()
                            }

                    val databaseLocations = DatabaseLocations(gpsTracking.getLongitude(), gpsTracking.getLatitude(), System.currentTimeMillis())

                    databaseReference.child(FirebaseHelper.TRACKING).child(uid).child("location").setValue(databaseLocations)

                    databaseReference.child("current_location").child(friendId).child(uid).removeValue()
                }
            }

            dismiss()
        }

        return dialog
    }
}
