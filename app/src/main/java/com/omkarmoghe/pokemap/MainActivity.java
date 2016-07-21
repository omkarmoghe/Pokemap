package com.omkarmoghe.pokemap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.omkarmoghe.pokemap.common.BaseActivity;
import com.omkarmoghe.pokemap.login.RequestCredentialsDialogFragment;
import com.omkarmoghe.pokemap.map.MapWrapperFragment;
import com.omkarmoghe.pokemap.settings.SettingsActivity;
import com.omkarmoghe.pokemap.utils.PokemapSharedPreferences;

public class MainActivity extends BaseActivity {
    private static final String TAG = "Pokemap";

    private PokemapSharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = new PokemapSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, MapWrapperFragment.newInstance())
                .addToBackStack(null)
                .commit();
        login();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_relogin) {
            login();
        }

        return super.onOptionsItemSelected(item);
    }


    private void login() {
        if (!pref.isUsernameSet() || !pref.isPasswordSet()) {
            requestLoginCredentials();
        } else {
            nianticManager.login(pref.getUsername(), pref.getPassword(), this);
        }
    }

    private void requestLoginCredentials() {
        getSupportFragmentManager().beginTransaction().add(RequestCredentialsDialogFragment.newInstance(
                new RequestCredentialsDialogFragment.Listener() {
                    @Override
                    public void credentialsIntroduced(String username, String password) {
                        pref.setUsername(username);
                        pref.setPassword(password);
                        login();
                    }
                }), "request_credentials").commit();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // TODO: test all this shit on a 6.0+ phone lmfao
        switch (requestCode) {
            case 703:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission granted");
                }
                break;
        }
    }
}
