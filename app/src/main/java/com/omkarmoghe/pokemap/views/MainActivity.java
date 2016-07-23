package com.omkarmoghe.pokemap.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.EventLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
<<<<<<< HEAD:app/src/main/java/com/omkarmoghe/pokemap/views/MainActivity.java
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.models.events.LoginEventResult;
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
=======
import com.omkarmoghe.pokemap.common.AbstractNianticEventListener;
import com.omkarmoghe.pokemap.common.BaseActivity;
import com.omkarmoghe.pokemap.common.Notifier;
import com.omkarmoghe.pokemap.login.RequestCredentialsDialogFragment;
import com.omkarmoghe.pokemap.map.LocationManager;
import com.omkarmoghe.pokemap.map.MapWrapperFragment;
import com.omkarmoghe.pokemap.network.NianticManager;
import com.omkarmoghe.pokemap.settings.SettingsActivity;
import com.omkarmoghe.pokemap.app_preferences.PokemapAppPreferences;
import com.omkarmoghe.pokemap.app_preferences.PokemapSharedPreferences;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;

import java.util.List;

import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass;
>>>>>>> refs/remotes/omkarmoghe/dev:app/src/main/java/com/omkarmoghe/pokemap/MainActivity.java

public class MainActivity extends BaseActivity {
    private static final String TAG = "Pokemap";

    private PokemapAppPreferences pref;

    //region Lifecycle Methods
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
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //region Menu Methods
    @Override
    public void onResume() {
        super.onResume();
        Notifier.instance().addListener(mEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        Notifier.instance().removeListener(mEventListener);
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

<<<<<<< HEAD:app/src/main/java/com/omkarmoghe/pokemap/views/MainActivity.java
=======
    //endregion



>>>>>>> refs/remotes/omkarmoghe/dev:app/src/main/java/com/omkarmoghe/pokemap/MainActivity.java
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
<<<<<<< HEAD:app/src/main/java/com/omkarmoghe/pokemap/views/MainActivity.java
=======
    //endregion

>>>>>>> refs/remotes/omkarmoghe/dev:app/src/main/java/com/omkarmoghe/pokemap/MainActivity.java

    private void login() {
        if (!pref.isUsernameSet() || !pref.isPasswordSet()) {
            requestLoginCredentials();
        } else {
            nianticManager.login(pref.getUsername(), pref.getPassword());
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

<<<<<<< HEAD:app/src/main/java/com/omkarmoghe/pokemap/views/MainActivity.java
    /**
     * Called whenever a LoginEventResult is posted to the bus. Originates from LoginTask.java
     *
     * @param result Results of a log in attempt
     */
    @Subscribe
    public void onEvent(LoginEventResult result) {
        if (result.isLoggedIn()) {
            Toast.makeText(this, "You have logged in successfully.", Toast.LENGTH_LONG).show();
            LatLng latLng = LocationManager.getInstance(MainActivity.this).getLocation();
            nianticManager.getCatchablePokemon(latLng.latitude, latLng.longitude, 0D);
        } else {
            Toast.makeText(this, "Could not log in. Make sure your credentials are correct.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called whenever a ServerUnreachableEvent is posted to the bus. Posted when the server cannot be reached
     *
     * @param event The event information
     */
    @Subscribe
    public void onEvent(ServerUnreachableEvent event) {
        Toast.makeText(this, "Unable to contact the Pokemon GO servers. The servers may be down.", Toast.LENGTH_LONG).show();
    }

    /**
     * Called whenever a TokenExpiredEvent is posted to the bus. Posted when the token from the login expired.
     *
     * @param event The event information
     */
    @Subscribe
    public void onEvent(TokenExpiredEvent event) {
        Toast.makeText(this, "The login token has expired. Getting a new one.", Toast.LENGTH_LONG).show();
        login();
    }
=======

    private NianticManager.Listener mEventListener = new AbstractNianticEventListener(){
        @Override
        public void onOperationFailure(Exception ex) {
            super.onOperationFailure(ex);
            Toast.makeText(MainActivity.this, "Error Occured: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLogin(RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo info, PokemonGo pokemonGo) {
            super.onLogin(info, pokemonGo);
            LatLng latLng = LocationManager.getInstance(MainActivity.this).getLocation();
            nianticManager.fetchCatchablePokemon(latLng.latitude, latLng.longitude, 0D);
            Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCatchablePokemonFound(List<CatchablePokemon> pokemons) {
            super.onCatchablePokemonFound(pokemons);
            Toast.makeText(getApplicationContext(), "Found " + pokemons.size() + " Catchable Pokemon", Toast.LENGTH_SHORT).show();
        }
    };
>>>>>>> refs/remotes/omkarmoghe/dev:app/src/main/java/com/omkarmoghe/pokemap/MainActivity.java
}
