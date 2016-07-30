package com.omkarmoghe.pokemap.views;

import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.omkarmoghe.pokemap.R;
import com.omkarmoghe.pokemap.controllers.service.PokemonNotificationService;
import com.omkarmoghe.pokemap.helpers.MapHelper;
import com.omkarmoghe.pokemap.models.events.ClearMapEvent;
import com.omkarmoghe.pokemap.models.events.InternalExceptionEvent;
import com.omkarmoghe.pokemap.models.events.LoginEventResult;
import com.omkarmoghe.pokemap.models.events.SearchInPosition;
import com.omkarmoghe.pokemap.models.events.ServerUnreachableEvent;
import com.omkarmoghe.pokemap.controllers.map.LocationManager;
import com.omkarmoghe.pokemap.views.map.MapWrapperFragment;
import com.omkarmoghe.pokemap.views.settings.SettingsActivity;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapAppPreferences;
import com.omkarmoghe.pokemap.controllers.app_preferences.PokemapSharedPreferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "Pokemap";
    private static final String MAP_FRAGMENT_TAG = "MapFragment";

    private boolean skipNotificationServer;
    private PokemapAppPreferences pref;
    private SharedPreferences sharedPref;
    private int themeId;

    private Snackbar _pokeSnackbar;

    public void snackMe(String message,int duration){
        if (null == _pokeSnackbar || _pokeSnackbar.getDuration() != duration){
            View rootView = findViewById(R.id.main_container);
            _pokeSnackbar = Snackbar.make(rootView,"",duration);
        }
        _pokeSnackbar.setText(message);
        _pokeSnackbar.show();
    }
    public void snackMe(String message){
        snackMe(message, Snackbar.LENGTH_LONG);
    }

    //region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = this.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE);
        themeId = sharedPref.getInt(getString(R.string.pref_theme_no_action_bar), R.style.AppTheme_NoActionBar);
        setTheme(themeId);
        setContentView(R.layout.activity_main);

        pref = new PokemapSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MapWrapperFragment mapWrapperFragment = (MapWrapperFragment) fragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG);
        if(mapWrapperFragment == null) {
            mapWrapperFragment = MapWrapperFragment.newInstance();
        }
        fragmentManager.beginTransaction().replace(R.id.main_container,mapWrapperFragment, MAP_FRAGMENT_TAG)
                .commit();

        if(pref.isServiceEnabled()){
            startNotificationService();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);

        if(pref.isServiceEnabled()) {
            stopNotificationService();
        }

        // If the theme has changed, recreate the activity.
        if(themeId != sharedPref.getInt(getString(R.string.pref_theme_no_action_bar), R.style.AppTheme_NoActionBar)) {
            recreate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);

        if(!skipNotificationServer && pref.isServiceEnabled()){
            startNotificationService();
        }

    }

    //endregion

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
            skipNotificationServer = true;
            startActivityForResult(new Intent(this, SettingsActivity.class),0);
        } else if (id == R.id.action_clear) {
            EventBus.getDefault().post(new ClearMapEvent());
        } else if (id == R.id.action_logout) {
            showLogoutPrompt();
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    private void showLogoutPrompt() {
        new AlertDialog.Builder(this).setTitle(R.string.action_logout).setMessage(R.string.logout_prompt_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void logout() {
        skipNotificationServer = true;
        pref.clearLoginCredentials();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        skipNotificationServer = false;
    }

    @Override
    public void onBackPressed() {
        skipNotificationServer = true;
        finish();
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

    private void startNotificationService(){

        // check for if the service is already running
        if (PokemonNotificationService.isRunning()) {
            stopNotificationService();
        }

        Intent intent = new Intent(this, PokemonNotificationService.class);
        startService(intent);
    }

    private void stopNotificationService() {
        Intent intent = new Intent(this, PokemonNotificationService.class);
        stopService(intent);
    }

    /**
     * Triggers a first pokemon scan after a successful login
     *
     * @param result Results of a log in attempt
     */
    @Subscribe
    public void onEvent(LoginEventResult result) {

        if (result.isLoggedIn()) {

            LatLng latLng = LocationManager.getInstance(MainActivity.this).getLocation();

            if (latLng != null) {
                nianticManager.getCatchablePokemon(latLng.latitude, latLng.longitude, 0D);
            } else {
                Snackbar.make(findViewById(R.id.root), getString(R.string.toast_login_error), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Called whenever a use whats to search pokemons on a different position
     *
     * @param event PoJo with LatLng obj
     */
    @Subscribe
    public void onEvent(SearchInPosition event) {
        List<LatLng> list = MapHelper.getSearchArea(event.getSteps(), new LatLng(event.getPosition().latitude, event.getPosition().longitude));
        snackMe(getString(R.string.toast_searching));

        nianticManager.getGyms(event.getPosition().latitude, event.getPosition().longitude, 0D);
        nianticManager.getPokeStops(event.getPosition().latitude, event.getPosition().longitude, 0D);
        nianticManager.getLuredPokemon(event.getPosition().latitude, event.getPosition().longitude, 0D);

        for (LatLng p : list) {
            nianticManager.getCatchablePokemon(p.latitude, p.longitude, 0D);
        }
    }

    /**
     * Called whenever a ServerUnreachableEvent is posted to the bus. Posted when the server cannot be reached
     *
     * @param event The event information
     */
    @Subscribe
    public void onEvent(ServerUnreachableEvent event) {
        Snackbar.make(findViewById(R.id.root), getString(R.string.toast_server_unreachable), Snackbar.LENGTH_LONG).show();
        event.getE().printStackTrace();
    }

    /**
     * Called whenever a InternalExceptionEvent is posted to the bus. Posted when the server cannot be reached
     *
     * @param event The event information
     */
    @Subscribe
    public void onEvent(InternalExceptionEvent event) {
        event.getE().printStackTrace();
        Snackbar.make(findViewById(R.id.root), getString(R.string.toast_internal_error), Snackbar.LENGTH_LONG).show();
    }

}
