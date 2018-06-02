package uk.co.appsbystudio.geoshare.setup

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth

import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.ProfileSelectionResult
import uk.co.appsbystudio.geoshare.utils.dialog.ProfilePictureOptions
import uk.co.appsbystudio.geoshare.setup.fragments.GetStartedFragment
import uk.co.appsbystudio.geoshare.setup.fragments.permissions.PermissionsFragment
import uk.co.appsbystudio.geoshare.setup.fragments.radius.RadiusSetupFragment
import uk.co.appsbystudio.geoshare.setup.fragments.profile.SetupProfileFragment
import uk.co.appsbystudio.geoshare.utils.ui.NoSwipeViewPager

class InitialSetupActivity : AppCompatActivity(), InitialSetupView {

    private var initialSetupPresenter: InitialSetupPresenter? = null
    private var sharedPreferences: SharedPreferences? = null

    private var viewPager: NoSwipeViewPager? = null

    private val PERMS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_setup)

        PreferenceManager.setDefaultValues(this, R.xml.pref_main, false)

        initialSetupPresenter = InitialSetupPresenterImpl(this)
        initialSetupPresenter?.addDeviceToken()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        viewPager = findViewById(R.id.view_pager)

        val fragmentPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                return when (position) {
                    0 -> GetStartedFragment()
                    1 -> PermissionsFragment()
                    2 -> SetupProfileFragment()
                    3 -> RadiusSetupFragment()
                    else -> null
                }
            }

            override fun getCount(): Int {
                return 4
            }
        }

        viewPager?.offscreenPageLimit = 3
        viewPager?.setPagingEnabled(false)
        viewPager?.adapter = fragmentPagerAdapter
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        ProfileSelectionResult(this).profilePictureResult(this, requestCode, resultCode, data, FirebaseAuth.getInstance().currentUser?.uid)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialSetupPresenter?.onPermissionsResult()
            } else {
                initialSetupPresenter?.onError(getString(R.string.generic_error))
            }
        }
    }

    override fun onBackPressed() {
        if (viewPager?.currentItem != 0) {
            this.onBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onFinish(radius: Int) {
        sharedPreferences?.edit()?.putInt("nearby_radius", radius)?.apply()
        sharedPreferences?.edit()?.putBoolean("first_run", false)?.apply()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onNext() {
        viewPager?.currentItem = viewPager?.currentItem?.plus(1)!!
    }

    override fun onBack() {
        viewPager?.currentItem = viewPager?.currentItem?.minus(1)!!
    }

    override fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun requestPermissions() {
        requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMS)
    }

    override fun onShowProfileDialog() {
        ProfilePictureOptions().show(fragmentManager, "profile_dialog")
    }

    override fun onError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}
