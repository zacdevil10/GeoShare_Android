package uk.co.appsbystudio.geoshare.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import uk.co.appsbystudio.geoshare.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_main);

        Preference dialogPreference = getPreferenceScreen().findPreference("nearby_radius");

        if (dialogPreference.getSharedPreferences().getBoolean("ringtone_silent", true)) {
            findPreference("ringtone").setEnabled(false);
            findPreference("ringtone_vibrate").setEnabled(false);
        }

        dialogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        //TODO: Update info where needed
        if (s.equals("ringtone_silent")) {
            if (sharedPreferences.getBoolean("ringtone_silent", true)) {
                findPreference("ringtone").setEnabled(false);
                findPreference("ringtone_vibrate").setEnabled(false);
            } else {
                findPreference("ringtone").setEnabled(true);
                findPreference("ringtone_vibrate").setEnabled(true);
            }
        }
    }
}
