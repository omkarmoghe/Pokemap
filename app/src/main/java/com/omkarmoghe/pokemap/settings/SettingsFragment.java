package com.omkarmoghe.pokemap.settings;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.omkarmoghe.pokemap.R;

public class SettingsFragment extends PreferenceFragment {

    SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        Preference usernamePref = findPreference(getString(R.string.pref_username));
        usernamePref.setSummary(pref.getString(getString(R.string.pref_username), getString(R.string.pref_default_username)));
    }
}
