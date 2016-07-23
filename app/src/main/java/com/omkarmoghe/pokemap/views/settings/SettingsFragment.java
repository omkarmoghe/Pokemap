package com.omkarmoghe.pokemap.views.settings;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.omkarmoghe.pokemap.R;

public class SettingsFragment extends PreferenceFragment {

    private SharedPreferences pref;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String usernameKey = getString(R.string.pref_username);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        final Preference usernamePref = findPreference(usernameKey);
        usernamePref.setSummary(pref.getString(usernameKey, getString(R.string.pref_default_username)));

        // Create change listener
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals(usernameKey))
                    usernamePref.setSummary(pref.getString(getString(R.string.pref_username), getString(R.string.pref_default_username)));
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register change listener
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unregister change listener
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }
}
