package uk.co.appsbystudio.geoshare.friends.profile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_profile.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.friends.profile.friends.ProfileFriendsFragment
import uk.co.appsbystudio.geoshare.friends.profile.info.ProfileInfoFragment
import uk.co.appsbystudio.geoshare.friends.profile.staticmap.ProfileStaticMapFragment
import uk.co.appsbystudio.geoshare.utils.ShowMarkerPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper
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

        text_name_profile.text = MainActivity.friendNames[uid]

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
        }

        image_back_button_profile.setOnClickListener {
            finish()
        }

        button_remove_friend_profile.setOnClickListener({
            presenter?.removeFriendDialog(uid)
        })
    }

    override fun showDialog(uid: String?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.run {
            setMessage("Are you sure you want to remove this person from your friends list?")
            setPositiveButton("OK", { dialogInterface, i ->
                presenter?.removeFriend(uid)
            })
            setNegativeButton("Cancel", { dialogInterface, i ->
                dialogInterface.dismiss()
            })
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
