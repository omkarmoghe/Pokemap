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
        // Create Theme button to link to Theme Fragment
        Preference btnTheme = (Preference) getPreferenceManager().findPreference(getString(R.string.pref_theme_button_key));
        if (btnTheme != null) {
            btnTheme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), ThemeActivity.class);
                    startActivity(intent);
                    return true;
                }
            });
        }

        Preference btnDirections = (Preference) getPreferenceManager().findPreference(getString(R.string.pref_directions_button_key));
        if (btnDirections != null) {
            btnDirections.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), DirectionsActivity.class);
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
