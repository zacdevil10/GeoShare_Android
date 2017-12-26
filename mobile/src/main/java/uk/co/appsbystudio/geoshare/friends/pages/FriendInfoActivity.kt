package uk.co.appsbystudio.geoshare.friends.pages

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.firebase.AddFriendsInfo

class FriendInfoActivity : AppCompatActivity() {

    private var name: String? = null
    private var userId: String? = null

    private var auth: FirebaseAuth? = null
    private var ref: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_info)

        auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        ref = database.reference

        val bundle = intent.extras
        if (bundle != null) {
            name = bundle.getString("name")
            userId = bundle.getString("uid")
        }

        //ImageView backdropImage = findViewById(R.id.infoBackdropImage);
        val profileImage = findViewById<CircleImageView>(R.id.avatar)

        val imageBitmap = BitmapFactory.decodeFile(cacheDir.toString() + "/" + userId + ".png")
        profileImage.setImageBitmap(imageBitmap)

        (findViewById<View>(R.id.name) as TextView).text = name

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            if (auth!!.currentUser != null) {
                val outgoing = AddFriendsInfo(true)
                ref!!.child("pending").child(auth!!.currentUser!!.uid).child(userId!!).setValue(outgoing)
                val incoming = AddFriendsInfo(false)
                ref!!.child("pending").child(userId!!).child(auth!!.currentUser!!.uid).setValue(incoming)
            }
        }
    }
}
