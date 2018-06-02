package uk.co.appsbystudio.geoshare.friends.pages

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.Application
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.profile.profileadapter.ProfilePagerAdapter
import uk.co.appsbystudio.geoshare.utils.ProfileUtils

class Profile : AppCompatActivity() {

    private var uid: String? = null
    private var name: String? = null

    private var auth: FirebaseAuth? = null
    private var ref: DatabaseReference? = null

    private var trackingPreferences: SharedPreferences? = null
    private var showOnMapPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val bundle = intent.extras

        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        ref = database.reference

        trackingPreferences = Application.getContext().getSharedPreferences("tracking", MODE_PRIVATE)
        showOnMapPreferences = Application.getContext().getSharedPreferences("showOnMap", MODE_PRIVATE)

        if (bundle != null) {
            uid = bundle.getString("uid")
            name = bundle.getString("name")
        }

        val backButton: ImageView = findViewById(R.id.back)

        backButton.setOnClickListener {
            finish()
        }

        val avatar: CircleImageView = findViewById(R.id.avatar)
        val nameView: TextView = findViewById(R.id.name)
        val addRemoveButton: Button = findViewById(R.id.add_remove_friend)
        val profileViewPager: ViewPager = findViewById(R.id.profile_view_pager)

        ProfileUtils.setProfilePicture(uid, avatar)

        if (MainActivity.friendNames.contains(uid)) {
            // Is a friend
            nameView.text = MainActivity.friendNames[uid]
            profileViewPager.adapter = ProfilePagerAdapter(supportFragmentManager, 3, uid!!)
        }

        addRemoveButton.setOnClickListener({
            showFriendOptionsDialog()
        })
    }

    private fun showFriendOptionsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setMessage("Are you sure you want to remove this person from your friends list?")
                .setPositiveButton("OK", { dialogInterface, i ->
                        removeFriend()
                    })
                .setNegativeButton("Cancel", { dialogInterface, i ->
                    println("CANCELED!")
                })

        val dialog: AlertDialog = builder.create()

        dialog.show()
    }

    private fun removeFriend() {
        if (auth?.currentUser != null) {
            ref?.child("friends")?.child(auth!!.currentUser!!.uid)?.child(uid!!)?.removeValue()
                    ?.addOnSuccessListener({
                        finish()
                    })
                    ?.addOnFailureListener({ Toast.makeText(this, "Could not remove friend", Toast.LENGTH_SHORT).show() })
            if (trackingPreferences?.contains(uid)!!) trackingPreferences?.edit()?.remove(uid)?.apply()
            if (trackingPreferences?.contains(uid)!!) showOnMapPreferences?.edit()?.remove(uid)?.apply()
        }
    }
}
