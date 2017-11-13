package uk.co.appsbystudio.geoshare.utils.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import uk.co.appsbystudio.geoshare.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    Preference displayNamePreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_main);

        displayNamePreference = getPreferenceScreen().findPreference("display_name");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        displayNamePreference.setSummary(sharedPreferences.getString("display_name", "DEFAULT"));

        Preference profilePicture = getPreferenceScreen().findPreference("profile_picture");
        Preference deleteAccount = getPreferenceScreen().findPreference("delete_user");

        profilePicture.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                profilePictureSettings();
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("display_name")) {
            displayNamePreference.setSummary(sharedPreferences.getString("display_name", ""));
        }
    }

    private void deleteAccountDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment deleteDialog = new DeleteUser();
        deleteDialog.show(fragmentManager, "");
    }

    private void profilePictureSettings() {
        FragmentManager fragmentManager = getFragmentManager();
        DialogFragment profileDialog = new ProfilePictureOptions();
        profileDialog.show(fragmentManager, "");
    }
}
