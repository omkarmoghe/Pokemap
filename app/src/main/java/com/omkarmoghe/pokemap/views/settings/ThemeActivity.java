package com.omkarmoghe.pokemap.views.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.omkarmoghe.pokemap.R;

public class ThemeActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private int themeId;

    private String PREF_ID;
    private String PREF_ID_NO_ACTION_BAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PREF_ID = getString(R.string.pref_theme);
        PREF_ID_NO_ACTION_BAR = getString(R.string.pref_theme_no_action_bar);

        sharedPref = this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        themeId = sharedPref.getInt(getString(R.string.pref_theme), R.style.AppTheme);
        setTheme(themeId);

        setTitle(getString(R.string.preset_themes_title));
        setContentView(R.layout.activity_theme);

        RadioButton r1 = (RadioButton) findViewById(R.id.radioButton1);
        RadioButton r2 = (RadioButton) findViewById(R.id.radioButton2);
        RadioButton r3 = (RadioButton) findViewById(R.id.radioButton3);
        RadioButton r4 = (RadioButton) findViewById(R.id.radioButton4);
        RadioButton r5 = (RadioButton) findViewById(R.id.radioButton5);

        switch (themeId) {
            case R.style.AppThemeSquirtle:
                r1.setChecked(true);
                break;
            case R.style.AppThemeCharmander:
                r2.setChecked(true);
                break;
            case R.style.AppThemeBulbasaur:
                r3.setChecked(true);
                break;
            case R.style.AppThemePikachu:
                r4.setChecked(true);
                break;
            case R.style.AppTheme:
                r5.setChecked(true);
                break;
            default:
                break;
        }
    }

    public void onRadioButtonClicked(View v) {

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        boolean checked = ((RadioButton) v).isChecked();


        switch(v.getId()) {
            case R.id.radioButton1:
                if(checked) {
                    editor.putInt(PREF_ID, R.style.AppThemeSquirtle);
                    editor.putInt(PREF_ID_NO_ACTION_BAR, R.style.AppThemeSquirtle_NoActionBar);
                    editor.apply();
                }
                break;
            case R.id.radioButton2:
                if(checked) {
                    editor.putInt(PREF_ID, R.style.AppThemeCharmander);
                    editor.putInt(PREF_ID_NO_ACTION_BAR, R.style.AppThemeCharmander_NoActionBar);
                    editor.apply();
                }
                break;
            case R.id.radioButton3:
                if(checked) {
                    editor.putInt(PREF_ID, R.style.AppThemeBulbasaur);
                    editor.putInt(PREF_ID_NO_ACTION_BAR, R.style.AppThemeBulbasaur_NoActionBar);
                    editor.apply();
                }
                break;
            case R.id.radioButton4:
                if(checked) {
                    editor.putInt(PREF_ID, R.style.AppThemePikachu);
                    editor.putInt(PREF_ID_NO_ACTION_BAR, R.style.AppThemePikachu_NoActionBar);
                    editor.apply();
                }
                break;
            case R.id.radioButton5:
                if(checked) {
                    editor.putInt(PREF_ID, R.style.AppTheme);
                    editor.putInt(PREF_ID_NO_ACTION_BAR, R.style.AppTheme_NoActionBar);
                    editor.apply();
                }
                break;
        }
    }
}
