package com.omkarmoghe.pokemap.views.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.omkarmoghe.pokemap.R;

public class SettingsFragment extends PreferenceFragment {

    private SharedPreferences pref;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        final Preference usernamePref = findPreference(getString(R.string.pref_username_key));
        usernamePref.setSummary(pref.getString(getString(R.string.pref_username_key), getString(R.string.pref_default_username)));

        final Preference passwordPref = findPreference(getString(R.string.pref_password_key));
        passwordPref.setSummary(pref.getString(getString(R.string.pref_password_key).replaceAll(".", "*"), getString(R.string.pref_default_password).replaceAll(".", "*")));

        // Create change listener
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if(s.equals(getString(R.string.pref_username_key)))
                    usernamePref.setSummary(pref.getString(getString(R.string.pref_username_key), getString(R.string.pref_default_username)));
                else if(s.equals(getString(R.string.pref_password_key)))
                    passwordPref.setSummary(pref.getString(getString(R.string.pref_password_key).replaceAll(".", "*"), getString(R.string.pref_default_password).replaceAll(".", "*")));
            }
        };

        // Create Theme button to link to Theme Fragment
        Preference button = (Preference) getPreferenceManager().findPreference(getString(R.string.pref_theme_button_key));
        if (button != null) {
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), ThemeActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register change listener
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister change listener
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }
}
