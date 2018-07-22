package uk.co.appsbystudio.geoshare.setup

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_initial_setup.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.setup.fragments.GetStartedFragment
import uk.co.appsbystudio.geoshare.setup.fragments.permissions.PermissionsFragment
import uk.co.appsbystudio.geoshare.setup.fragments.profile.SetupProfileFragment
import uk.co.appsbystudio.geoshare.setup.fragments.radius.RadiusSetupFragment
import uk.co.appsbystudio.geoshare.utils.ProfileSelectionResult
import uk.co.appsbystudio.geoshare.utils.SettingsPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.dialog.ProfilePictureOptions

class InitialSetupActivity : AppCompatActivity(), InitialSetupView {

    private var presenter: InitialSetupPresenter? = null
    private var settingsPreferencesHelper: SettingsPreferencesHelper? = null

    companion object {
        private const val PERMS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_setup)

        PreferenceManager.setDefaultValues(this, R.xml.pref_main, false)

        presenter = InitialSetupPresenterImpl(this)
        presenter?.addDeviceToken()

        settingsPreferencesHelper = SettingsPreferencesHelper(PreferenceManager.getDefaultSharedPreferences(this))

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

        view_pager?.apply {
            offscreenPageLimit = 3
            setPagingEnabled(false)
            adapter = fragmentPagerAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        ProfileSelectionResult(initial = this).profilePictureResult(this, requestCode, resultCode, data, FirebaseAuth.getInstance().currentUser?.uid)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter?.onPermissionsResult()
            } else {
                presenter?.onError(getString(R.string.generic_error))
            }
        }
    }

    override fun onBackPressed() {
        if (view_pager?.currentItem != 0) {
            this.onBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onFinish(radius: Int) {
        settingsPreferencesHelper?.run {
            setDisplayName(FirebaseAuth.getInstance().currentUser?.displayName)
            setNearbyRadius(radius)
            setFirstRun(false)
        }

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onNext() {
        view_pager?.currentItem = view_pager?.currentItem?.plus(1)!!
    }

    override fun onBack() {
        view_pager?.currentItem = view_pager?.currentItem?.minus(1)!!
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

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
