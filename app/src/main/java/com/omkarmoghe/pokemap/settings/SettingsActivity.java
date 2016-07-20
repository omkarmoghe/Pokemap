package com.omkarmoghe.pokemap.settings;

import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.omkarmoghe.pokemap.R;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }
}
