package com.omkarmoghe.pokemap.views.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.omkarmoghe.pokemap.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        // Create Theme button to link to Theme Fragment
        Preference button = getPreferenceManager().findPreference(getString(R.string.pref_theme_button_key));
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
}
