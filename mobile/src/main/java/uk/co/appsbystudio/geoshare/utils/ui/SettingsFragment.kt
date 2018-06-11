package uk.co.appsbystudio.geoshare.utils.ui

import android.app.DialogFragment
import android.app.FragmentManager
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
            profilePictureSettings()
            false
        }

        changePassword.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            changePasswordDialog()
            false
        }

        deleteAccount.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            deleteAccountDialog()
            false
        }
    }

    private fun deleteAccountDialog() {
        val fragmentManager = fragmentManager
        val deleteDialog = DeleteUser()
        deleteDialog.show(fragmentManager, "")
    }

    private fun changePasswordDialog() {
        val fragmentManager = fragmentManager
        val deleteDialog = ChangePasswordDialog()
        deleteDialog.show(fragmentManager, "")
    }

    private fun profilePictureSettings() {
        val fragmentManager = fragmentManager
        val profileDialog = ProfilePictureOptions()
        profileDialog.show(fragmentManager, "")
    }
}
