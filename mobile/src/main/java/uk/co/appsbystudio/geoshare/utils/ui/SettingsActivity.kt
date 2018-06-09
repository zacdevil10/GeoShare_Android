package uk.co.appsbystudio.geoshare.utils.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.ProfileSelectionResult
import uk.co.appsbystudio.geoshare.utils.firebase.UserInformation

class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var userId: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_manager)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        fragmentManager.beginTransaction().replace(R.id.frame_content_main, SettingsFragment()).commit()

        PreferenceManager.getDefaultSharedPreferences(applicationContext).registerOnSharedPreferenceChangeListener(this)

        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            userId = auth.currentUser!!.uid
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        ProfileSelectionResult().profilePictureResult(this, requestCode, resultCode, data, userId)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == "display_name") {
            val name = sharedPreferences.getString(key, "DEFAULT")
            val userInfo = UserInformation(name, name.toLowerCase())
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) FirebaseDatabase.getInstance().reference.child("users").child(user.uid).setValue(userInfo)

            val profileChangeRequest = UserProfileChangeRequest.Builder().setDisplayName(name).build()

            user?.updateProfile(profileChangeRequest)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(applicationContext).unregisterOnSharedPreferenceChangeListener(this)
    }
}
