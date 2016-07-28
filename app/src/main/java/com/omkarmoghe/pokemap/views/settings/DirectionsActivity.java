package com.omkarmoghe.pokemap.views.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omkarmoghe.pokemap.R;

/**
 * Created by carsten on 27-07-16.
 */

public class DirectionsActivity extends AppCompatActivity {

    public static final String PREF_KEY = "DirectionsAPIKey";

    private LinearLayout parentLayout;
    private SharedPreferences sharedPref;
    private EditText inputAPIKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        int themeId = sharedPref.getInt(getString(R.string.pref_theme), R.style.AppTheme);
        setTheme(themeId);

        setTitle("Directions");
        setContentView(R.layout.activity_directions);

        parentLayout = (LinearLayout) findViewById(R.id.activity_directions);

        TextView txtAPIConsole = (TextView) findViewById(R.id.txt_api_console);
        txtAPIConsole.setMovementMethod(LinkMovementMethod.getInstance());
        String apiConsoleHtml = "<a href='https://console.developers.google.com/flows/enableapi?apiid=directions_backend&keyType=SERVER_SIDE'>Google API Console</a>";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtAPIConsole.setText(Html.fromHtml(apiConsoleHtml, Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtAPIConsole.setText(Html.fromHtml(apiConsoleHtml));
        }

        inputAPIKey = (EditText) findViewById(R.id.inp_directions_api_key);
        inputAPIKey.setText(sharedPref.getString(PREF_KEY, ""));
        inputAPIKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_GO || id == EditorInfo.IME_NULL) {
                    activateDirectionsAPIKey();
                    return true;
                }

                return false;
            }
        });

        Button btnActivateDirections = (Button) findViewById(R.id.btn_register_directions_api);
        btnActivateDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateDirectionsAPIKey();
            }
        });
    }

    private void activateDirectionsAPIKey() {
        String apiKey = inputAPIKey.getText().toString();

        if (TextUtils.isEmpty(apiKey)) {
            Snackbar.make(parentLayout, "API Key required", Snackbar.LENGTH_SHORT).show();
        } else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(PREF_KEY, inputAPIKey.getText().toString());
            editor.apply();

            Toast.makeText(this, "Server API Key registered", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
