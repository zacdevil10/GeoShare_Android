package uk.co.appsbystudio.geoshare.utils.ui

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.dialog.ChangePasswordDialog
import uk.co.appsbystudio.geoshare.utils.dialog.DeleteUser
import uk.co.appsbystudio.geoshare.utils.dialog.ProfilePictureOptions

class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.pref_main)

        val profilePicture = preferenceScreen.findPreference("profile_picture")
        val changePassword = preferenceScreen.findPreference("change_password")
        val deleteAccount = preferenceScreen.findPreference("delete_user")

        profilePicture.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            ProfilePictureOptions().show(fragmentManager, "")
            false
        }

        changePassword.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            ChangePasswordDialog().show(fragmentManager, "")
            false
        }

        deleteAccount.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            DeleteUser().show(fragmentManager, "")
            false
        }
    }
}
