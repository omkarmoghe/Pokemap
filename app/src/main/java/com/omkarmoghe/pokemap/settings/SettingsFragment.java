package com.omkarmoghe.pokemap.settings;

import android.preference.PreferenceFragment;
import android.os.Bundle;

import com.omkarmoghe.pokemap.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
