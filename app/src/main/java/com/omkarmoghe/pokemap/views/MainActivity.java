package com.omkarmoghe.pokemap.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.models.events.InternalExceptionEvent;
import com.omkarmoghe.pokemap.models.events.LoginEventResult;
import com.omkarmoghe.pokemap.models.events.SearchInPosition;
import com.omkarmoghe.pokemap.models.events.ServerUnreachableEvent;
import com.omkarmoghe.pokemap.models.events.TokenExpiredEvent;
import com.omkarmoghe.pokemap.views.login.RequestCredentialsDialogFragment;
import com.omkarmoghe.pokemap.controllers.map.LocationManager;
import com.omkarmoghe.pokemap.views.map.MapWrapperFragment;
import com.omkarmoghe.pokemap.views.settings.SettingsActivity;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapAppPreferences;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends BaseActivity {
    private static final String TAG = "Pokemap";
    private static final String MAP_FRAGMENT_TAG = "MapFragment";


    private PokemapAppPreferences pref;

    public static Toast toast;

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = new PokemapSharedPreferences(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MapWrapperFragment mapWrapperFragment = (MapWrapperFragment) fragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG);
        if(mapWrapperFragment == null) {
            mapWrapperFragment = MapWrapperFragment.newInstance();
        }
        fragmentManager.beginTransaction().replace(R.id.main_container,mapWrapperFragment, MAP_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    //region Menu Methods
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

    private void login() {
        if (!pref.isUsernameSet() || !pref.isPasswordSet()) {
            requestLoginCredentials();
        } else {
            nianticManager.login(pref.getUsername(), pref.getPassword());
        }
    }

    private void requestLoginCredentials() {
        getSupportFragmentManager().beginTransaction().add(RequestCredentialsDialogFragment.newInstance(null), "request_credentials").commit();
    }

    /**
     * Called whenever a LoginEventResult is posted to the bus. Originates from LoginTask.java
     *
     * @param result Results of a log in attempt
     */
    @Subscribe
    public void onEvent(LoginEventResult result) {
        if (result.isLoggedIn()) {
            toast.setText("You have logged in successfully.");
            toast.show();
            LatLng latLng = LocationManager.getInstance(MainActivity.this).getLocation();

            if (latLng != null) {
                nianticManager.getCatchablePokemon(latLng.latitude, latLng.longitude, 0D);
            }
        } else {
            toast.cancel();
            toast.setText("Could not log in. Make sure your credentials are correct.");
            toast.show();
        }
    }

    /**
     * Called whenever a use whats to search pokemons on a different position
     *
     * @param event PoJo with LatLng obj
     */
    @Subscribe
    public void onEvent(SearchInPosition event) {
        toast.setText("Searching...");
        toast.show();
        nianticManager.getCatchablePokemon(event.getPosition().latitude, event.getPosition().longitude, 0D);
    }

    /**
     * Called whenever a ServerUnreachableEvent is posted to the bus. Posted when the server cannot be reached
     *
     * @param event The event information
     */
    @Subscribe
    public void onEvent(ServerUnreachableEvent event) {

        event.getE().printStackTrace();

        toast.setText("Unable to contact the Pokemon GO servers. The servers may be down.");
        toast.show();
    }

    /**
     * Called whenever a TokenExpiredEvent is posted to the bus. Posted when the token from the login expired.
     *
     * @param event The event information
     */
    @Subscribe
    public void onEvent(TokenExpiredEvent event) {

        toast.setText("The login token has expired. Getting a new one.");
        toast.show();
        login();
    }

    /**
     * Called whenever a InternalExceptionEvent is posted to the bus. Posted when the server cannot be reached
     *
     * @param event The event information
     */
    @Subscribe
    public void onEvent(InternalExceptionEvent event) {

        event.getE().printStackTrace();

        Toast.makeText(this, "An internal error occurred. This might happen when you are offline or the servers are down.", Toast.LENGTH_LONG).show();
    }

}
