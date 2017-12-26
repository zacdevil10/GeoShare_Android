package uk.co.appsbystudio.geoshare.friends.pages

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.MainActivity
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.friendsadapter.ProfilePagerAdapter
import uk.co.appsbystudio.geoshare.utils.ProfileUtils

class Profile : AppCompatActivity() {

    private var uid: String? = null
    private var name: String? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val bundle = intent.extras

        auth = FirebaseAuth.getInstance()

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
            addRemoveButton.text = "FRIENDS"
            profileViewPager.adapter = ProfilePagerAdapter(supportFragmentManager, 3, uid!!)
        } else if (name != null) {
            // Is not a friend
            nameView.text = name
            profileViewPager.adapter = ProfilePagerAdapter(supportFragmentManager, 2, uid!!)
        }

        if (MainActivity.friendsId.contains(uid)) {
            addRemoveButton.text = "UNFRIEND"
        } else if (MainActivity.pendingId.contains(uid)) {
            addRemoveButton.text = "REQUEST PENDING"
        }
    }
}
