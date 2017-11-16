package uk.co.appsbystudio.geoshare.utils.ui;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.dialog.ChangePasswordDialog;
import uk.co.appsbystudio.geoshare.utils.dialog.DeleteUser;
import uk.co.appsbystudio.geoshare.utils.dialog.ProfilePictureOptions;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_main);

        Preference profilePicture = getPreferenceScreen().findPreference("profile_picture");
        Preference changePassword = getPreferenceScreen().findPreference("change_password");
        Preference deleteAccount = getPreferenceScreen().findPreference("delete_user");

        profilePicture.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                profilePictureSettings();
                return false;
            }
        });

        changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                changePasswordDialog();
                return false;
            }
        });

        deleteAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                deleteAccountDialog();
                return false;
            }
        });
    }

    private void deleteAccountDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment deleteDialog = new DeleteUser();
        deleteDialog.show(fragmentManager, "");
    }

    private void changePasswordDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment deleteDialog = new ChangePasswordDialog();
        deleteDialog.show(fragmentManager, "");
    }

    private void profilePictureSettings() {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment profileDialog = new ProfilePictureOptions();
        profileDialog.show(fragmentManager, "");
    }
}
