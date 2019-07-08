package uk.co.appsbystudio.geoshare.friends.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_profile.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.profile.friends.ProfileFriendsFragment
import uk.co.appsbystudio.geoshare.friends.profile.info.ProfileInfoFragment
import uk.co.appsbystudio.geoshare.friends.profile.staticmap.ProfileStaticMapFragment
import uk.co.appsbystudio.geoshare.utils.ShowMarkerPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.GetUserFromDatabase
import uk.co.appsbystudio.geoshare.utils.setProfilePicture

class ProfileActivity : AppCompatActivity(), ProfileView {

    private var presenter: ProfilePresenter? = null

    private var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        presenter = ProfilePresenterImpl(this,
                TrackingPreferencesHelper(getSharedPreferences("tracking", MODE_PRIVATE)),
                ShowMarkerPreferencesHelper(getSharedPreferences("showOnMap", MODE_PRIVATE)))

        val bundle = intent.extras

        if (bundle != null) {
            uid = bundle.getString("uid")
        }

        image_avatar_profile.setProfilePicture(uid, this.cacheDir.toString())

        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.USERS}/$uid").addListenerForSingleValueEvent(GetUserFromDatabase(text_name_profile))

        view_pager_profile.apply {
            adapter = object : FragmentPagerAdapter(supportFragmentManager) {
                override fun getItem(position: Int): Fragment? {
                    return when (position) {
                        0 -> ProfileInfoFragment.newInstance(uid)
                        1 -> ProfileStaticMapFragment.newInstance(uid)
                        2 -> ProfileFriendsFragment.newInstance(uid)
                        else -> null
                    }
                }

                override fun getCount(): Int {
                    return 3
                }
            }
            offscreenPageLimit = 2
            pageMargin = (8 * context.resources.displayMetrics.density).toInt()
        }

        view_left_profile.setOnClickListener {
            presenter?.setViewPagerPosition(view_pager_profile.currentItem - 1)
        }

        view_right_profile.setOnClickListener {
            presenter?.setViewPagerPosition(view_pager_profile.currentItem + 1)
        }

        image_back_button_profile.setOnClickListener {
            finish()
        }

        button_remove_friend_profile.setOnClickListener {
            presenter?.removeFriendDialog(uid)
        }
    }

    override fun setPosition(position: Int) {
        view_pager_profile.currentItem = position
    }

    override fun showDialog(uid: String?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.run {
            setMessage("Are you sure you want to remove this person from your friends list?")
            setPositiveButton("OK") { dialogInterface, i ->
                presenter?.removeFriend(uid)
            }
            setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            create()
            show()
        }
    }

    override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun closeProfile() {
        finish()
    }
}
