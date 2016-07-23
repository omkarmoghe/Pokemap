package com.omkarmoghe.pokemap.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.controllers.map.LocationManager;
import com.omkarmoghe.pokemap.controllers.net.tasks.GetCatchablePokemonTask;
import com.omkarmoghe.pokemap.controllers.net.tasks.LoginTask;
import com.omkarmoghe.pokemap.models.events.LoginEventResult;
import com.omkarmoghe.pokemap.models.events.ServerUnreachableEvent;
import com.omkarmoghe.pokemap.models.events.TokenExpiredEvent;
import com.omkarmoghe.pokemap.views.login.RequestCredentialsDialogFragment;
import com.omkarmoghe.pokemap.views.map.MapWrapperFragment;
import com.omkarmoghe.pokemap.views.settings.SettingsActivity;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapAppPreferences;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity {
    private static final String TAG = "Pokemap";

    private PokemapAppPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
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
            new LoginTask(pref.getUsername(), pref.getPassword()).execute();
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
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 703:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission granted");
                }
                break;
        }
    }

    /**
     * Called whenever a LoginEventResult is posted to the bus. Originates from LoginTask.java
     *
     * @param result Results of a log in attempt
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEventResult result) {
        if (result.isLoggedIn()) {
            Toast.makeText(this, "You have logged in successfully.", Toast.LENGTH_LONG).show();
            LatLng latLng = LocationManager.getInstance(MainActivity.this).getLocation();
            new GetCatchablePokemonTask(latLng.latitude, latLng.longitude, 10.0D).execute();
        } else {
            Toast.makeText(this, "Could not log in. Make sure your credentials are correct.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called whenever a ServerUnreachableEvent is posted to the bus. Posted when the server cannot be reached
     *
     * @param event The event information
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ServerUnreachableEvent event) {
        Toast.makeText(this, "Unable to contact the Pokemon GO servers. The servers may be down.", Toast.LENGTH_LONG).show();
    }

    /**
     * Called whenever a TokenExpiredEvent is posted to the bus. Posted when the token from the login expired.
     *
     * @param event The event information
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TokenExpiredEvent event) {
        Toast.makeText(this, "The login token has expired. Getting a new one.", Toast.LENGTH_LONG).show();
        login();
    }

}
