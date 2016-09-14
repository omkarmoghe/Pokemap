package com.omkarmoghe.pokemap.views.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.omkarmoghe.pokemap.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private int themeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        themeId = sharedPref.getInt(getString(R.string.pref_theme_no_action_bar), R.style.AppTheme_NoActionBar);
        setTheme(themeId);
        setTitle(getString(R.string.action_settings));
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.settings_content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume(); // Check if theme has changed, if so then restart the activity.
        if(themeId != sharedPref.getInt(getString(R.string.pref_theme_no_action_bar), R.style.AppTheme_NoActionBar)) {
            recreate();
        }
    }
}
