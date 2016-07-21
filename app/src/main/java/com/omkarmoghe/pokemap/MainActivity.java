package com.omkarmoghe.pokemap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.omkarmoghe.pokemap.app.App;
import com.omkarmoghe.pokemap.common.BaseActivity;
import com.omkarmoghe.pokemap.map.MapWrapperFragment;
import com.omkarmoghe.pokemap.settings.SettingsActivity;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String TAG = "Pokemap";

    // fragments
    private MapWrapperFragment mMapWrapperFragment;

    // Preferences
    @Inject
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((App) getApplication()).getMainComponent().inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMapWrapperFragment = MapWrapperFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, mMapWrapperFragment)
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

        String username = pref.getString(getString(R.string.pref_username), "");
        String password = pref.getString(getString(R.string.pref_password), "");

        Log.d(TAG, "Username: " + username);
        nianticManager.login(username, password, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

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
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
